
import UIKit

class PopUpViewController: UIViewController {

    @IBOutlet weak var PopUpLabel: UILabel!
    @IBOutlet weak var dispTextView: UITextView!
    @IBOutlet weak var dismissButton: UIButton!
    
    let json = GlobalVar.json
    var jsonArrayNames = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()

        let notificationCenter = NotificationCenter.default
        notificationCenter.addObserver(self, selector: #selector(appMovedToBackground), name: NSNotification.Name.UIApplicationWillResignActive, object: nil)
        
        PopUpLabel.text = GlobalVar.popupLabel
        switch GlobalVar.popupLabel {
        case "Agent Results":
            jsonArrayNames = ["results","AgentResults"]
            displayData()
            
        case "Events Logged":
            jsonArrayNames = ["reporting"]
            displayData()
            
        case "Credentials":
            jsonArrayNames = ["creds"]
            displayData()
        default:
            print("Something went wrong!")
            
        }
    }

    @objc func appMovedToBackground() {
        if self.presentedViewController == nil {
            
        } else {
            let thePresentedVC : UIViewController? = self.presentedViewController as UIViewController?
            if thePresentedVC != nil {
                if let thePresentedVCAsAlertController : UIAlertController = thePresentedVC as? UIAlertController {
                    self.dismiss(animated: true, completion: nil)
                } else {
                }
            }
        }
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "splashScene") as UIViewController
        self.present(newViewController, animated: true, completion: nil)
    }
    
    @IBAction func okayButtonPressed(_ sender: Any) {
        dismiss(animated: true)
    }
    
    func displayData() {
        if jsonArrayNames.count > 1 {
                for item in json[jsonArrayNames[0]].arrayValue {
                self.dispTextView.text = item.rawString()!.replacingOccurrences(of: "{", with: "").replacingOccurrences(of: "}", with: "")
                    .replacingOccurrences(of: ",", with: "").replacingOccurrences(of: "\\\\r\\\\n", with: "\n").replacingOccurrences(of: "  ", with: "").replacingOccurrences(of: "   ", with: "")
                
                for innerItem in item[jsonArrayNames[1]] {
                    self.dispTextView.text = self.dispTextView.text + "\r\n" + innerItem.0.replacingOccurrences(of: "{", with: "").replacingOccurrences(of: "}", with: "")
                        .replacingOccurrences(of: ",", with: "\n")  + innerItem.1.rawString()!.replacingOccurrences(of: "{", with: "").replacingOccurrences(of: "}", with: "")
                            .replacingOccurrences(of: ",", with: "").replacingOccurrences(of: "\\\\r\\\\n", with: "\n").replacingOccurrences(of: "  ", with: "").replacingOccurrences(of: "   ", with: "")
                }
            }
        } else {
            for item in json[jsonArrayNames[0]].arrayValue {
                self.dispTextView.text = item.rawString()!.replacingOccurrences(of: "{", with: "").replacingOccurrences(of: "}", with: "")
                    .replacingOccurrences(of: ",", with: "").replacingOccurrences(of: "\\\\r\\\\n", with: "\n").replacingOccurrences(of: "  ", with: "").replacingOccurrences(of: "   ", with: "")
            }
        }
    }
}
