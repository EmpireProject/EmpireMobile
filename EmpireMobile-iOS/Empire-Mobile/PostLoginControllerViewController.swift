
import UIKit
import Alamofire
import SwiftyJSON
import PKHUD

class PostLoginControllerViewController: UIViewController {

    @IBOutlet weak var PostTextView: UITextView!
    @IBOutlet weak var LogoutButton: UIButton!
    @IBOutlet weak var getListeners: UIButton!
    @IBOutlet weak var agentsButton: UIButton!
    @IBOutlet var TopView: UIView!
    
    
    let address = GlobalVar.serverAddr
    let token = GlobalVar.token
    let group = DispatchGroup()
    let screenSize: CGRect = UIScreen.main.bounds
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let borderColor : UIColor = UIColor(red: 0, green: 255, blue: 13, alpha: 1.0)
        
        
        let notificationCenter = NotificationCenter.default
        notificationCenter.addObserver(self, selector: #selector(appMovedToBackground), name: NSNotification.Name.UIApplicationWillResignActive, object: nil)
        
        PostTextView.layer.borderWidth = 0.5
        PostTextView.layer.borderColor = borderColor.cgColor
        PostTextView.layer.cornerRadius = 5.0
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
    
    
    @IBAction func logoutButtonIsPressed(_ sender: Any) {
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "LoginViewController") as UIViewController
        self.present(newViewController, animated: true, completion: nil)
    }

    
    @IBAction func getListeners(_ sender: Any) {
        let urlString = "\(self.address)listeners?token=\(self.token)"
        let method: HTTPMethod = HTTPMethod.get
        let name: String = #function
        getRequest(urlString: urlString, method: method, funcName: name)
    }
    
    @IBAction func createListener(_ sender: Any) {
        let alertControl = UIAlertController(title: "Create Listener", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let listName: String = alertControl.textFields![1].text!
            let host: String = alertControl.textFields![2].text!
            let delay: String = alertControl.textFields![3].text!
            let type: String = alertControl.textFields![0].text!
            let port: String = alertControl.textFields![3].text!
            let parameters: [String:Any] = ["Name": listName, "Host": host, "Port": port, "DefaultDelay": delay]
            let method: HTTPMethod = .post
            let funcName: String = #function
            let urlString = "\(self.address)listeners/\(type)?token=\(self.token)"
            self.postRequest(urlString: urlString, method: method, parameters: parameters, name: funcName)
        }
        
        let cancel = UIAlertAction(title: "Cancel", style: .cancel) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.placeholder = "listener type"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.placeholder = "name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect

        })
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.placeholder = "host"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.placeholder = "port"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.placeholder = "delay"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func agentsView(_ sender: Any) {
        let urlString = "\(self.address)modules?token=\(self.token)"
        let method: HTTPMethod = .get
        let funcName: String = #function
        self.getRequest(urlString: urlString, method: method, funcName: funcName)
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "AgentsViewController") as UIViewController
        self.present(newViewController, animated: true, completion: nil)
    }
    
    @IBAction func killListener(_ sender: Any) {
        let alertControl = UIAlertController(title: "Kill Listener", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let agent: String = alertControl.textFields![0].text!
            let method: HTTPMethod = .delete
            let funcName: String = #function
            let urlString = "\(self.address)listeners/\(agent)?token=\(self.token)"
            self.getRequest(urlString: urlString, method: method, funcName: funcName)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .cancel) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.placeholder = "Listener Name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    
    
    func getRequest(urlString: String, method: HTTPMethod, funcName: String) {
        self.group.enter()
        let URL: String = urlString
        let method: HTTPMethod = method
        let address = GlobalVar.IP
        GlobalVar.funcName = funcName
        let serverTrustPolicies: [String: ServerTrustPolicy] = [address: .disableEvaluation]
        
        let sessionManager = SessionManager(
            serverTrustPolicyManager: ServerTrustPolicyManager(policies: serverTrustPolicies)
        )
        sessionManager.request(URL, method: method, encoding: JSONEncoding.default, headers: ["Accept": "application/json"])
            .responseJSON { responseData in
                sessionManager.session.invalidateAndCancel()
                
                if let data = responseData.data, let utf8Text = String(data: data, encoding: .utf8) {
                    if let data = utf8Text.data(using: .utf8) {
                        if let json = try? JSON(data: data) {
                            GlobalVar.json = json
                        }
                    }
                }
                self.group.leave()
                }
                self.group.notify(queue: DispatchQueue.main) {
                    self.parseData()
                }
        }
    
    
    func postRequest(urlString: String, method: HTTPMethod, parameters: [String:Any], name: String) {
        self.group.enter()
        let URL: String = urlString
        let method: HTTPMethod = method
        let parameters = parameters
        let address = GlobalVar.IP
        GlobalVar.funcName = name
        
        let serverTrustPolicies: [String: ServerTrustPolicy] = [address: .disableEvaluation]
        
        let sessionManager = SessionManager(
            serverTrustPolicyManager: ServerTrustPolicyManager(policies: serverTrustPolicies)
        )
        
        
        sessionManager.request(URL, method: method, parameters: parameters, encoding: JSONEncoding.default, headers: ["Accept": "application/json"])
            .responseJSON { responseData in
                sessionManager.session.invalidateAndCancel()
                
                if let data = responseData.data, let utf8Text = String(data: data, encoding: .utf8) {
                    if let data = utf8Text.data(using: .utf8) {
                        if let json = try? JSON(data: data) {
                            GlobalVar.json = json
                        }
                    }
                }
                self.group.leave()
                }
        self.group.notify(queue: DispatchQueue.main) {
            self.parseData()
        }
    }
    
    func parseData() {
        let dataName = GlobalVar.funcName
        let json = GlobalVar.json
        switch dataName {
        case "getListeners":
            for item in json["listeners"].arrayValue {
                DispatchQueue.main.async {
                    self.PostTextView.text = self.PostTextView.text + "\r\n" + item.rawString()!.replacingOccurrences(of: "{", with: "").replacingOccurrences(of: "}", with: "")
                        .replacingOccurrences(of: ",", with: "").replacingOccurrences(of: "\\\\r\\\\n", with: "\n").replacingOccurrences(of: "  ", with: "").replacingOccurrences(of: "   ", with: "").replacingOccurrences(of: "/", with: "")
                }
            }
        case "createListener":
            for (key, _) in json {
                HUD.flash(.label(key), delay: 2)
            }
            
        case "killListener":
            for (key, _) in json {
                HUD.flash(.label(key), delay: 2)
            }
 
        case "agentsView":
            for item in json["modules"].arrayValue {
                if item["Name"].stringValue.contains("powershell") {
                    GlobalVar.powershell.append(item["Name"].stringValue)
                }else {
                    if item["Name"].stringValue.contains("python") {
                        GlobalVar.python.append(item["Name"].stringValue)
                    } else {
                        if item["Name"].stringValue.contains("exfiltration") {
                            GlobalVar.exfil.append(item["Name"].stringValue)
                        } else {
                            if item["Name"].stringValue.contains("external") {
                                GlobalVar.external.append(item["Name"].stringValue)
                            }
                        }
                    }
                }
            }
            
        default:
            print("something went wrong!")
        }
    }
    
}
