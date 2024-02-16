//
//  ContentView.swift
//  SessionlessExample
//
//  Created by Zach Babb on 2/9/24.
//

import SwiftUI
import Sessionless


struct ContentView: View {
    
    let sessionless = Sessionless()
    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundColor(.accentColor)
            Text("Hello, world!")
            Button("Press me") {
                sessionless.generateKeys()
                let keys = sessionless.getKeys()
                print(keys)
                let sig = sessionless.sign(message: "foobar")
                print(sig)
                let verified = sessionless.verifySignature(signature: sig!, message: "foobar", publicKey: keys!.publicKey)
                print(verified)
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
