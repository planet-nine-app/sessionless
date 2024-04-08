use indexmap::IndexMap;
use sessionless::PublicKey;
use std::collections::HashMap;
use tokio::sync::Mutex;

lazy_static! {
    static ref DATABASE: Mutex<IndexMap<String, User>> = Mutex::new(IndexMap::new());
}

struct User {
    public_key: PublicKey,
    associated: HashMap<String, PublicKey>,
    value: Option<String>,
}

pub struct Database;

impl Database {
    pub async fn save_user(uuid: String, public_key: PublicKey) {
        let user = User {
            public_key,
            associated: Default::default(),
            value: None,
        };

        let mut lock = DATABASE.lock().await;
        lock.insert(uuid, user);
        drop(lock);
    }

    pub async fn get_user_pub_key(uuid: impl AsRef<str>) -> Option<PublicKey> {
        DATABASE
            .lock()
            .await
            .get(uuid.as_ref())
            .map(|user| user.public_key)
    }

    pub async fn associate_key(
        uuid: impl AsRef<str>,
        uuid_asc: String,
        public_key_asc: PublicKey,
    ) -> Option<()> {
        let mut lock = DATABASE.lock().await;

        let user = lock.get_mut(uuid.as_ref())?;
        user.associated.insert(uuid_asc, public_key_asc);

        drop(lock);
        Some(())
    }

    pub async fn get_user_by_associated_key(uuid_asc: impl AsRef<str>) -> Option<PublicKey> {
        let lock = DATABASE.lock().await;
        let uuid_asc = uuid_asc.as_ref();

        for (_, user) in &*lock {
            if let Some(public_key_asc) = user.associated.get(uuid_asc) {
                return Some(*public_key_asc);
            }
        }

        None
    }

    pub async fn save_value(uuid: impl AsRef<str>, value: String) -> Option<()> {
        let mut lock = DATABASE.lock().await;

        let user = lock.get_mut(uuid.as_ref())?;
        let _ = user.value.insert(value);

        drop(lock);
        None
    }

    pub async fn get_value(uuid: impl AsRef<str>) -> Option<String> {
        DATABASE
            .lock()
            .await
            .get(uuid.as_ref())
            .map(|user| user.value.clone())?
    }
}
