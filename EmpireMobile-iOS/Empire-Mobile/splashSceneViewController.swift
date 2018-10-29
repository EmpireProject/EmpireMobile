
import UIKit

class splashSceneViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        let notificationCenter = NotificationCenter.default
        notificationCenter.addObserver(self, selector: #selector(appReturnedfromBackground), name: NSNotification.Name.UIApplicationDidBecomeActive, object: nil)
    }

    @objc func appReturnedfromBackground() {
        dismiss(animated: true)
    }

}
