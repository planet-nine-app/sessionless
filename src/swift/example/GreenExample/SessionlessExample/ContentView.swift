
//
//  ContentView.swift
//  SessionlessExample
//
//  Created by Zach Babb on 2/9/24.
//

import SwiftUI
import Sessionless
//import Particles
//import ParticlesPresets

struct User: Codable {
    let uuid: String
    let welcomeMessage: String
}

struct Coolness: Codable {
    let doubleCool: String
}

struct ContentView: View {
    
    let sessionless = Sessionless()
    var associateKey: AssociateKey
    @State var enteredText = ""
    @State var welcomeMessage = ""
    @State var uuid = ""
    @State private var showingAlert = false
    @State private var associateAlert = false
    @State private var successAlert = false
    @State private var coolness = Coolness(doubleCool: "foo")
    
    struct ExampleTextField: View {
        @Binding var enteredText: String
        
        var body: some View {
            TextField("Enter Text", text: $enteredText)
        }
    }
    
    var body: some View {
        
        VStack {
            Text("""
                Welcome to the iOS Swift example app. This will connect to locally run servers on ports 3000
                 (primary) and 3001 (secondary).
                """
            ).padding(.all, 8)
                .alert("The server thinks your \(coolness.doubleCool)", isPresented: $showingAlert) {
                Button("OK") { }
                }
                .alert("Blue would like to associate with you. Will you allow it?", isPresented: $associateAlert) {
                    Button("OK") {
                        guard let publicKey = sessionless.getKeys()?.publicKey else { return }
                        let timestamp = "".getTime()
                        let message =
                        """
                        {"uuid":"\(uuid)","timestamp":"\(timestamp)","pubKey":"\(publicKey)"}
                        """
                        
                        guard let signature = sessionless.sign(message: message) else { return }
                        
                        let urlString =
                        """
                        green://associate?uuid=\(uuid)&timestamp=\(timestamp)&pubKey=\(publicKey)&signature=\(signature.r)
                        """
                        guard let url = URL(string: urlString) else { return }
                        UIApplication.shared.open(url)
                    }
                    Button("Cancel", role: .cancel) {}
                }
                .alert("Key associated", isPresented: $showingAlert) {
                    Button("OK") { }
                }
            Text("Enter some text here.")
                .padding(.all, 8)
            ExampleTextField(enteredText: $enteredText)
                .padding(.all, 8)
            //ParticleSystem {
              //  Particle {
                    Button("Register") {
                        Task {
                            await Network.register(enteredText: $enteredText.wrappedValue, callback: { err, data in
                                if let err = err {
                                    print("error")
                                    print(err)
                                    return
                                }
                                guard let data = data else { return }
                                do {
                                    let user = try JSONDecoder().decode(User.self, from: data)
                                    uuid = user.uuid
                                    welcomeMessage = user.welcomeMessage
                                } catch {
                                    return
                                }
                            })
                        }
                    }.padding(.all, 8)
                        .background(.purple)
                        .foregroundColor(.white)
                        /*.dissolve(if: {
                            return welcomeMessage != ""
                        }())*/
              //  }
            //}.statePersistent("foo", refreshesViews: false)
            
            if uuid.count > 3 && welcomeMessage.count > 3 {
                Text("\(welcomeMessage) now you can do cool stuff")
                    .padding(.all, 8)
                if false {
                    Button("Do Cool Stuff") {
                        Task {
                            await Network.doCoolStuff { err, data in
                                if let err = err {
                                    print("error")
                                    print(err)
                                    return
                                }
                                guard let data = data else { return }
                                do {
                                    let resp = try JSONDecoder().decode(Coolness.self, from: data)
                                    coolness = resp
                                    showingAlert = true
                                } catch {
                                    print(err)
                                }
                            }
                        }
                    }.background(.purple)
                        .foregroundColor(.white)
                } else {
                    Button("Join Blue") {
                        guard let url = URL(string: "blue://foo?bar") else { return }
                        UIApplication.shared.open(url)
                    }
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(.green)
        .onOpenURL { url in
            guard let query = url.query() else { return }
            print(query)
            if query.contains("bar") {
                print("associateAlert should be true")
                associateAlert = true
            } else {
                let params = query.components(separatedBy: "&")
                let values = params.map { String($0.split(separator: "=")[1]) }
                let associateKey = AssociateKey(uuid: values[0], timestamp: values[1], pubKey: values[2], signature: values[3])
                Task {
                    await Network.associate(associateKey: associateKey) { error, user in
                        if let error = error {
                            print(error)
                            return
                        }
                        if let user = user {
                            successAlert = true
                        }
                    }
                }
            }
        }
    }
}

