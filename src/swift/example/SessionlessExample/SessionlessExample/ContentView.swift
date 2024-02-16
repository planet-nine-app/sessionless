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
    @State var enteredText = ""
    
    func put(urlString: String, payload: Data, callback: @escaping (Error?, Data?) -> Void) async {
        guard let url = URL(string: urlString) else { return }
        var request = URLRequest(url: url)
        request.httpMethod = "PUT"
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        do {
            let (data, _) = try await URLSession.shared.upload(for: request, from: payload)
            callback(nil, data)
        } catch {
            callback(error, nil)
        }
    }
    
    func register(enteredText: String) async {
        sessionless.generateKeys()
        guard let publicKey = sessionless.getKeys()?.publicKey else { return }
        let message = """
            {"publicKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"right now"}
            """
        let signature = sessionless.sign(message: message)
        
        let payload = """
            {"publicKey":"\(publicKey)","enteredText":"\(enteredText)","timestamp":"right now","signature":"\(signature)"}
            """
        guard let data = payload.data(using: .utf8) else { return }
        await put(urlString: "http://localhost:3000/register", payload: data) { err, data in
            if let err = err {
                print("error")
                print(err)
                return
            }
            print("huzzah")
        }
    }
    
    struct ExampleTextField: View {
        @Binding var enteredText: String
        
        var body: some View {
            TextField("Enter Text", text: $enteredText)
        }
    }
    
    var body: some View {
        VStack {
            Image(systemName: "globe")
                .imageScale(.large)
                .foregroundColor(.accentColor)
            Text("Hello, world!")
            ExampleTextField(enteredText: $enteredText)
            Button("Press me") {
                Task {
                    await register(enteredText: $enteredText.wrappedValue)
                }
            }
        }
        .padding()
    }
}

