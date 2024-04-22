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
        Dictionary<string, UserModel> users = JsonSerializer.Deserialize<Dictionary<string, UserModel>>(userString);
        users[user.uuid] = user;
        File.WriteAllText(usersPath, JsonSerializer.Serialize<Dictionary<string, UserModel>>(users));
    }

    public static UserModel getUser(string uuid) {
        var userString = File.ReadAllText(usersPath);
        Dictionary<string, UserModel> users = JsonSerializer.Deserialize<Dictionary<string, UserModel>>(userString);
        return users[uuid];
    }
}
