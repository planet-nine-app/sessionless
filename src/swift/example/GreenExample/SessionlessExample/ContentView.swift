
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
    let baseURL = "http://localhost:3000"
    let otherURL = "http://localhost:3001"
    @State var enteredText = ""
    @State var welcomeMessage = ""
    @State var uuid = ""
    @State private var showingAlert = false
    @State private var associateAlert = false
    @State private var successAlert = false
    @State private var spreadAlert = false
    @State private var coolness = Coolness(doubleCool: "foo")
    @State private var associated = false
    @State private var accentColor = 0
    @State private var secondaryButtonTextState = 0
    
    func secondaryButtonText() -> String {
        switch secondaryButtonTextState {
        case 0: return "Join Blue"
        case 1: return "Spread Green"
        default: return "Join Blue"
        }
    }
    
    func changeBackgroundColor() -> Color {
        switch accentColor {
        case 0: return Color.purple
        case 1: return Color.green
        case 2: return Color.blue
        default: return Color.purple
        }
    }
    
    
    
    struct ExampleTextField: View {
        @Binding var enteredText: String
        
        var body: some View {
            TextField("Enter Text", text: $enteredText)
        }
    }
    
    struct CustomButtonStyle: ButtonStyle {
        
        func makeBody(configuration: Configuration) -> some View {
            configuration.label
                .padding()
                .foregroundColor(.white)
                .cornerRadius(10)
                .opacity(configuration.isPressed ? 0.5 : 1)
        }
    }
    
    var body: some View {
        
        VStack {
            Text("""
                Welcome to the iOS Swift example app. This will connect to locally run servers on ports 3000
                 (primary) and 3001 (secondary).
                """
            ).padding(.all, 8)
                .alert("Green has been spread!", isPresented: $spreadAlert) {
                    Button("OK") {
                        
                    }
                }
                .alert("Keys Associated!", isPresented: $successAlert) {
                    Button("OK") {
                        secondaryButtonTextState = 1
                    }
                }
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
                        blue://associate?uuid=\(uuid)&timestamp=\(timestamp)&pubKey=\(publicKey)&signature=\(signature)
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
            Button() {
                Task {
                    await Network.register(baseURL: baseURL, enteredText: $enteredText.wrappedValue, callback: { err, data in
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
                            Persistence.saveUUID(uuid: uuid)
                        } catch {
                            return
                        }
                    })
                }
            } label: {
                Text("Register")
                    .frame(width: 160, height: 22)
            }.buttonStyle(CustomButtonStyle())
            .background(changeBackgroundColor())
            
            if uuid.count > 3 && welcomeMessage.count > 3 {
                Text("\(welcomeMessage) now you can do cool stuff")
                    .padding(.all, 8)
                if false {
                    Button("Do Cool Stuff") {
                        Task {
                            await Network.doCoolStuff(baseURL: baseURL) { err, data in
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
                    }.buttonStyle(CustomButtonStyle())
                    .background(changeBackgroundColor())

                } else {
                    if !associated {
                        Button() {
                            guard let url = URL(string: "blue://foo?bar=baz") else { return }
                            UIApplication.shared.open(url)
                        } label: {
                            Text(secondaryButtonText())
                                .frame(width: 160, height: 22)
                        }.buttonStyle(CustomButtonStyle())
                        .background(changeBackgroundColor())

                    } else {
                        Button() {
                            Task {
                                await Network.setValue(value: "green", baseURL: otherURL) { error, data in
                                    if let error = error {
                                        print(error)
                                        return
                                    }
                                    print("success")
                                    spreadAlert = true
                                }
                            }
                        } label: {
                            Text("Spread Green")
                                .frame(width: 160, height: 22)
                        }.buttonStyle(CustomButtonStyle())
                        .background(changeBackgroundColor())
                        .frame(width: 160, height: 48)
                    }
                    Button() {
                        Task {
                            await Network.getValue(baseURL: baseURL) { error, data in
                                if let error = error {
                                    print(error)
                                    return
                                }
                                accentColor = 2
                            }
                        }
                    } label: {
                        Text("Check For Blue")
                            .frame(width: 160, height: 22)
                    }.buttonStyle(CustomButtonStyle())
                    .background(changeBackgroundColor())
                    .frame(width: 160, height: 48)
                    .padding(.top, 24)
                }
            }
        }
        .frame(maxWidth: .infinity, maxHeight: .infinity)
        .background(.green)
        .onOpenURL { url in
            guard let query = url.query() else { return }
            if query.contains("bar") {
                associateAlert = true
            } else {
                let params = query.components(separatedBy: "&")
                let values = params.map { String($0.split(separator: "=")[1]) }
                let associateKey = AssociateKey(uuid: values[0], timestamp: values[1], pubKey: values[2], signature: values[3])
                Task {
                    await Network.associate(baseURL: baseURL, associateKey: associateKey) { error, user in
                        if let error = error {
                            print(error)
                            return
                        }
                        associated = true
                        successAlert = true
                    }
                }
            }
        }
    }
}

