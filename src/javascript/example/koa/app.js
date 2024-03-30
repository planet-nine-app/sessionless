import Koa from "koa";
import json from "koa-json";
import KoaRouter from "koa-router";
import KoaBody from "koa-body";
import bodyParser from "koa-bodyparser";
import sessionless from "sessionless-node";
import fs from "fs/promises";

const app = new Koa();
const router = new KoaRouter();

//JSON Prettier Middleware
app.use(json());


//Bodypaser Middleware
app.use(bodyParser({ enableTypes: ["json", "text", 'form', 'xml'] }));

//Router Middleware
app.use(router.routes()).use(router.allowedMethods());


//Helper methods
const getUsers = async () => {
  const usersString = await fs.readFile("./users.json");
  const users = JSON.parse(usersString);
  return users;
};

const saveUser = async (uuid, publicKey) => {
  let usersJson = await getUsers();
  let users = usersJson["users"];
  users.push({ uuid, publicKey });
  await fs.writeFile("./users.json", JSON.stringify({ users: users }), "utf8");
};

const findUserFromPublicKey = async (publicKey) => {
  try {
    const usersString = fs.readFileSync("./users.json");
    const users = JSON.parse(usersString);

    const targetUser = users.find((user) => user.publicKey === publicKey);
    if (targetUser) {
      return targetUser;
    } else {
      throw new Error("User not found");
    }
  } catch (error) {
    console.error("Error: ", error);
  }
};

const findUserFromUuid = (uuid) => {
  try {
    const usersString = fs.readFileSync("./users.json", "utf8");
    const users = JSON.parse(usersString);

    const targetUser = users.find((user) => user.uuid === uuid);
    if (targetUser) {
      return targetUser;
    } else {
      throw new Error("User not found");
    }
  } catch (error) {
    console.error("Error: ", error);
  }
};

const saveUserPost = (uuid, post) => {
  try {
    const user = findUserFromUuid(user);
    fs.writeFileSync("./posts.json", JSON.stringify({ uuid, post }));
  } catch (error) {
    console.error("Error: ", error);
  }
};
const findUserPosts = (uuid) => {
  try {
    const postsString = fs.readFileSync("./posts.json", "utf8");
    const posts = JSON.parse(postsString);

    const postsByUser = posts.find((post) => post.uuid === uuid);
    if (postsByUser) {
      return postsByUser;
    } else {
      throw new Error("User has not posted.");
    }
  } catch (error) {
    console.error("Error: ", error);
  }
};

//Server routes
router.get("/users", getAllUsers);
router.post("/register", registerUser);

//Retrieves all users
async function getAllUsers(ctx) {
  try {
    const allUsers = await getUsers();
    ctx.body = allUsers;
  } catch (error) {
    ctx.throw(404, "No users found");
  }
}

//Register user
async function registerUser(ctx) {
  try {
    const payload = ctx.request.body;
    console.log(payload);
    ctx.body = payload;
  } catch (error) {
    ctx.throw(500, error);
  }
}

//Start server
app.listen(3000);
