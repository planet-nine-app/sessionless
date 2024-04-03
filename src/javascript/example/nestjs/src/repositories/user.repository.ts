import { readFile, writeFile } from "fs/promises";

export class UserRepository {
  static async getUsers() {
    const usersString = await readFile("./users.json");
    const users = JSON.parse(usersString.toString("utf-8"));
    return users;
  }

  static async saveUser(uuid: string, publicKey: string) {
    const usersJson = await UserRepository.getUsers();
    const users = usersJson.users;
    users.push({ uuid, publicKey });
    await writeFile("./users.json", JSON.stringify({ users }), "utf8");
  }

  static async findUserFromPublicKey(publicKey: string) {
    try {
      const usersString = await readFile("./users.json");
      const users = JSON.parse(usersString.toString("utf-8"));

      const targetUser = users.find((user) => user.publicKey === publicKey);
      if (targetUser) {
        return targetUser;
      } else {
        throw new Error("User not found");
      }
    } catch (error) {
      console.error("Error: ", error);
    }
  }

  static async findPublicKeyFromUuid(uuid: string) {
    try {
      const usersString = await readFile("./users.json", "utf8");
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
  }
}
