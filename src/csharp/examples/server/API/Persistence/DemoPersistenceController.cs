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

	// make nullable 
	Dictionary<string, UserModel>? users = JsonSerializer.Deserialize<Dictionary<string, UserModel>>(userString);

	// access only if is of type `Dictionary<string, UserModel>` 
	// + store in variable called `notnullable` or any name you want
	if (users is Dictionary<string, UserModel> notnullable) {
	    notnullable[user.uuid] = user;
		users[user.uuid] = user;
		File.WriteAllText(usersPath, JsonSerializer.Serialize<Dictionary<string, UserModel>>(users));
	}
    }

    public static UserModel getUser(string uuid) {
        var userString = File.ReadAllText(usersPath);
        Dictionary<string, UserModel> users = JsonSerializer.Deserialize<Dictionary<string, UserModel>>(userString);
        if(users is null) {
            throw new InvalidOperationException("No user file found.");
        }
        return users[uuid];
    }
}
