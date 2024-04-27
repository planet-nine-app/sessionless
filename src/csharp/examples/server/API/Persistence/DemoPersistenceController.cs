using Microsoft.AspNetCore.Mvc;
using System;
using System.Text.Json;
using System.IO;
using SessionlessExample.Server.Models;

namespace SessionlessExample.Server.Controllers;

class DemoPersistenceController {
    
    const string usersPath = "./users.json";
    public static void saveUser(UserModel user) {
        var userString = File.ReadAllText(usersPath);
        Dictionary<string, UserModel> users ...

// make nullable 
Dictionary<string, UserModel>? users = JsonSerializer.Deserialize<Dictionary<string, UserModel>>(userString);
// or use var simply to write less code
var users = JsonSerializer.Deserialize<Dictionary<string, UserModel>>(userString);

// access only if is of type `Dictionary<string, UserModel>` 
// + store in variable called `notnullable` or any name you want
if (users is Dictionary<string, UserModel> notnullable) {
    notnullable[user.uuid] = user;
        users[user.uuid] = user;
        File.WriteAllText(usersPath, JsonSerializer.Serialize<Dictionary<string, UserModel>>(users));
    }

    public static UserModel getUser(string uuid) {
        var userString = File.ReadAllText(usersPath);
        Dictionary<string, UserModel> users = JsonSerializer.Deserialize<Dictionary<string, UserModel>>(userString);
        if(users is null) {
            return new UserModel()
        }
        return users[uuid];
    }
}
