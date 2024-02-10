//
//  ContentView.swift
//  SessionlessExample
//
//  Created by Zach Babb on 2/9/24.
//

import SwiftUI


struct ContentView: View {
    
    let sessionless = Sessionless()
    func generateKeys() {
        var privateKey: String = "foo"
        do {
            privateKey = try sessionless.generateKeys(nil, nil)
        } catch {
            print(error)
        }
        
        print(privateKey)
    }
   
    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundColor(.accentColor)
            Text("Hello, world!")
            Button("Press me") {
                generateKeys()
            }
        }
        .padding()
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
