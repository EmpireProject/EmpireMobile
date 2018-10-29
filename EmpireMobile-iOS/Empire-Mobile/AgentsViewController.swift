
import UIKit
import Alamofire
import SwiftyJSON
import PKHUD

class AgentsViewController: UIViewController {

    @IBOutlet weak var listenersButton: UIButton!
    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var agentInfoTextView: UITextView!
    
    let address = GlobalVar.serverAddr
    let token = GlobalVar.token
    let group = DispatchGroup()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let notificationCenter = NotificationCenter.default
        notificationCenter.addObserver(self, selector: #selector(appMovedToBackground), name: NSNotification.Name.UIApplicationWillResignActive, object: nil)
        
        let borderColor : UIColor = UIColor(red: 0, green: 255, blue: 13, alpha: 1.0)
        
        containerView.layer.borderColor = borderColor.cgColor
        containerView.layer.borderWidth = 0.5
        
        agentInfoTextView.layer.borderWidth = 0.5
        agentInfoTextView.layer.borderColor = borderColor.cgColor
        agentInfoTextView.layer.cornerRadius = 5.0
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
    
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        if(segue.identifier == "embeddedContainer") {
            let embedVC = segue.destination as! AgentsChildViewController
            embedVC.delegate = self
        }
    }
    
    @IBAction func listersButtonPressed(_ sender: Any) {
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "PostLoginViewController") as UIViewController
        self.present(newViewController, animated: true, completion: nil)
    }
    
    @IBAction func powershellButtonPressed(_ sender: Any) {
        GlobalVar.modulesType = "powershell"
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "PickerViewController") as UIViewController
        self.present(newViewController, animated: true, completion: nil)
    }
    
    @IBAction func pythonButtonPressed(_ sender: Any) {
        GlobalVar.modulesType = "python"
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "PickerViewController") as UIViewController
        self.present(newViewController, animated: true, completion: nil)
    }
    
    @IBAction func exfilButtonPressed(_ sender: Any) {
        GlobalVar.modulesType = "exfil"
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "PickerViewController") as UIViewController
        self.present(newViewController, animated: true, completion: nil)
    }
    
    @IBAction func externalButtonPressed(_ sender: Any) {
        GlobalVar.modulesType = "external"
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "PickerViewController") as UIViewController
        self.present(newViewController, animated: true, completion: nil)
    }
    
    
    @IBAction func renameButtonPressed(_ sender: Any) {
        let alertControl = UIAlertController(title: "Rename Agent", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let agent = alertControl.textFields![0].text!
            let newName = alertControl.textFields![1].text!
            let parameters: [String:Any] = ["newname": newName]
            let method: HTTPMethod = .post
            let funcName: String = #function
            let urlString = "\(self.address)agents/\(agent)/rename?token=\(self.token)"
            self.postRequest(urlString: urlString, method: method, parameters: parameters, name: funcName)
        }
        
        let cancel = UIAlertAction(title: "Cancel", style: .cancel) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.text = GlobalVar.agentName
            textField.placeholder = "agent name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.placeholder = "new agent name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func executeButtonPressed(_ sender: Any) {
        let alertControl = UIAlertController(title: "Execute Shell Command", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let agent = alertControl.textFields![0].text!
            let command = alertControl.textFields![1].text!
            let parameters: [String:Any] = ["command": command]
            let method: HTTPMethod = .post
            let funcName: String = #function
            let urlString = "\(self.address)agents/\(agent)/shell?token=\(self.token)"
            self.postRequest(urlString: urlString, method: method, parameters: parameters, name: funcName)
        }
        
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.text = GlobalVar.agentName
            textField.placeholder = "agent name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.placeholder = "shell command"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func getResultsButtonPressed(_ sender: Any) {
        let alertControl = UIAlertController(title: "Get Results", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let agent = alertControl.textFields![0].text!
            let method: HTTPMethod = .get
            let funcName: String = #function
            let urlString = "\(self.address)agents/\(agent)/results?token=\(self.token)"
            self.getRequest(urlString: urlString, method: method, funcName: funcName)
        }
        
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.text = GlobalVar.agentName
            textField.placeholder = "agent name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func DeleteResultsButtonPressed(_ sender: Any) {
        let alertControl = UIAlertController(title: "Delete Results", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let agent = alertControl.textFields![0].text!
            let method: HTTPMethod = .delete
            let funcName: String = #function
            let urlString = "\(self.address)agents/\(agent)/results?token=\(self.token)"
            self.getRequest(urlString: urlString, method: method, funcName: funcName)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.text = GlobalVar.agentName
            textField.placeholder = "agent name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func EventsLoggedButtonPressed(_ sender: Any) {
        let alertControl = UIAlertController(title: "Events Logged", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let agent = alertControl.textFields![0].text!
            let method: HTTPMethod = .get
            let funcName: String = #function
            let urlString = "\(self.address)reporting/agent/\(agent)?token=\(self.token)"
            self.getRequest(urlString: urlString, method: method, funcName: funcName)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.text = GlobalVar.agentName
            textField.placeholder = "agent name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func getCredsButtonPressed(_ sender: Any) {
        let method: HTTPMethod = .get
        let funcName: String = #function
        let urlString = "\(self.address)creds?token=\(self.token)"
        self.getRequest(urlString: urlString, method: method, funcName: funcName)
    }
    
    @IBAction func killAgentButtonPressed(_ sender: Any) {
        let alertControl = UIAlertController(title: "Kill Agent", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let agent = alertControl.textFields![0].text!
            let method: HTTPMethod = .get
            let funcName: String = #function
            let urlString = "\(self.address)agents/\(agent)/kill?token=\(self.token)"
            self.getRequest(urlString: urlString, method: method, funcName: funcName)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.text = GlobalVar.agentName
            textField.placeholder = "agent name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func removeAgentButtonPressed(_ sender: Any) {
        let alertControl = UIAlertController(title: "Remove Agent", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let agent = alertControl.textFields![0].text!
            let method: HTTPMethod = .delete
            let funcName: String = #function
            let urlString = "\(self.address)agents/\(agent)?token=\(self.token)"
            self.getRequest(urlString: urlString, method: method, funcName: funcName)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.text = GlobalVar.agentName
            textField.placeholder = "agent name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func clearTasksButtonPressed(_ sender: Any) {
        let alertControl = UIAlertController(title: "Clear Tasks", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let agent = alertControl.textFields![0].text!
            let method: HTTPMethod = .get
            let funcName: String = #function
            let urlString = "\(self.address)agents/\(agent)/clear?token=\(self.token)"
            self.getRequest(urlString: urlString, method: method, funcName: funcName)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (ACTION) in
        }
        
        alertControl.addTextField(configurationHandler: { (textField) in
            textField.text = GlobalVar.agentName
            textField.placeholder = "agent name"
            textField.keyboardType = .default
            textField.borderStyle = .roundedRect
        })
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func removeStaleButtonPressed(_ sender: Any) {
        let alertControl = UIAlertController(title: "Remove Stale Agents?", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let method: HTTPMethod = .delete
            let funcName: String = #function
            let urlString = "\(self.address)agents/stale?token=\(self.token)"
            self.getRequest(urlString: urlString, method: method, funcName: funcName)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (ACTION) in
        }
        
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

        case "renameButtonPressed":
            for (key, _) in json {
                if key.contains("success") {
                    HUD.flash(.label("\(key)\n refresh your agents!"), delay: 2)
                } else {
                    HUD.flash(.label(json["error"].stringValue), delay: 4)
                }
            }
            
        case "executeButtonPressed":
            for (key, _) in json {
                if key.contains("success") {
                    HUD.flash(.label("\(key)"), delay: 2)
                } else {
                    HUD.flash(.label(json["error"].stringValue), delay: 4)
                }
            }
            
        case "getResultsButtonPressed":
            GlobalVar.popupLabel = "Agent Results"
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let popup = storyboard.instantiateViewController(withIdentifier: "PopUpVC")
            self.present(popup, animated: true)
            
        case "DeleteResultsButtonPressed":
            for (key, _) in json {
                if key.contains("success") {
                    HUD.flash(.label("\(key)"), delay: 2)
                } else {
                    HUD.flash(.label(json["error"].stringValue), delay: 4)
                }
            }
         
        case "EventsLoggedButtonPressed":
            GlobalVar.popupLabel = "Events Logged"
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let popup = storyboard.instantiateViewController(withIdentifier: "PopUpVC")
            self.present(popup, animated: true)
        
        case "getCredsButtonPressed":
            GlobalVar.popupLabel = "Credentials"
            let storyboard = UIStoryboard(name: "Main", bundle: nil)
            let popup = storyboard.instantiateViewController(withIdentifier: "PopUpVC")
            self.present(popup, animated: true)
            
        case "killAgentButtonPressed":
            for (key, _) in json {
                if key.contains("success") {
                    HUD.flash(.label("\(key)"), delay: 2)
                } else {
                    HUD.flash(.label(json["error"].stringValue), delay: 4)
                }
            }
          
        case "removeAgentButtonPressed":
            for (key, _) in json {
                if key.contains("success") {
                    HUD.flash(.label("\(key)\n refresh your agents!"), delay: 2)
                } else {
                    HUD.flash(.label(json["error"].stringValue), delay: 4)
                }
            }
         
        case "clearTasksButtonPressed":
            for (key, _) in json {
                if key.contains("success") {
                    HUD.flash(.label("\(key)"), delay: 2)
                } else {
                    HUD.flash(.label(json["error"].stringValue), delay: 4)
                }
            }
            
        case "removeStaleButtonPressed":
            for (key, _) in json {
                if key.contains("success") {
                    HUD.flash(.label("\(key)\n refresh your agents!"), delay: 2)
                } else {
                    HUD.flash(.label(json["error"].stringValue), delay: 4)
                }
            }
            
        default:
            print("something went wrong!")
        }
    }
}

extension AgentsViewController: settingLabels {
    func setLabels() {
        agentInfoTextView.textColor = UIColor.green
        for item in GlobalVar.staleNames {
            if item == GlobalVar.agentName {
                agentInfoTextView.textColor = UIColor.lightGray
            }
        }
        agentInfoTextView.text = "AGENT INFO \n name:  \(GlobalVar.agentName) \n hostname: \(GlobalVar.hostname) \n process id: \(GlobalVar.procID) \n external IP: \(GlobalVar.extIP) \n internal IP: \(GlobalVar.internalIP) \n listener: \(GlobalVar.listener) \n process name: \(GlobalVar.procName) \n username: \(GlobalVar.username) \n lastseen time: \(GlobalVar.lastseen) \n OS details: \(GlobalVar.osDetails) \n checkin time: \(GlobalVar.checkinTime) \n delay: \(GlobalVar.delay)"
    }
}
