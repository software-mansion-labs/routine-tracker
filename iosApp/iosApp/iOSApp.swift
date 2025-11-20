import SwiftUI
import ComposeApp

@main
struct iOSApp: App {
    init() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, err in
            print("Notification permission granted=\(granted) error=\(String(describing: err))")
        }
    }
    
    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
