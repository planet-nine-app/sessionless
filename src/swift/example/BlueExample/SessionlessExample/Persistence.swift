//
//  Persistence.swift
//  SessionlessExample
//
//  Created by Zach Babb on 4/4/24.
//

import Foundation

class Persistence {
    class func saveUUID(uuid: String) {
        UserDefaults.standard.setValue(uuid, forKey: "uuid")
    }
    
    class func getUUID() -> String {
        guard let uuid = UserDefaults.standard.value(forKey: "uuid") as? String else { return "" }
        return uuid
    }
}
