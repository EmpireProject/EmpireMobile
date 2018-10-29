
import UIKit
import Alamofire
import SwiftyJSON
import PKHUD

class PickerViewController: UIViewController, UIPickerViewDataSource, UIPickerViewDelegate {

    @IBOutlet weak var pickerView: UIPickerView!
    @IBOutlet weak var backButton: UIButton!
    @IBOutlet weak var headerLabel: UILabel!
    @IBOutlet weak var selectButton: UIButton!
    
    
    let group = DispatchGroup()
    let address = GlobalVar.serverAddr
    let token = GlobalVar.token
    var moduleNames = [String]()
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        pickerView.delegate = self
        pickerView.dataSource = self 
        
        let notificationCenter = NotificationCenter.default
        notificationCenter.addObserver(self, selector: #selector(appMovedToBackground), name: NSNotification.Name.UIApplicationWillResignActive, object: nil)
        
        switch GlobalVar.modulesType {
        case "powershell":
            moduleNames = GlobalVar.powershell.sorted()
            headerLabel.text = "Powershell Modules"
        case "python":
            moduleNames = GlobalVar.python.sorted()
            headerLabel.text = "Python Modules"
        case "exfil":
            moduleNames = GlobalVar.exfil.sorted()
            headerLabel.text = "Exfiltration Modules"
        case "external":
            moduleNames = GlobalVar.external.sorted()
            headerLabel.text = "External Modules"
        default:
            print("something went wrong in PickerView switch case")
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

    public func numberOfComponents(in pickerView: UIPickerView) -> Int {
        return 1
    }

    public func pickerView(_ pickerView: UIPickerView, numberOfRowsInComponent component: Int) -> Int {
        return moduleNames.count
    }
    
    func pickerView(_ pickerView: UIPickerView, titleForRow row: Int, forComponent component: Int) -> String? {
        return moduleNames[row]
    }
    
    func pickerView(_ pickerView: UIPickerView, didSelectRow row: Int, inComponent component: Int) {
        GlobalVar.moduleName = moduleNames[row]
    }
    
    @IBAction func selectButtonPressed(_ sender: Any) {
        if GlobalVar.moduleName == "" {
            HUD.flash(.label("Select a module first."), delay: 2)
        } else {}
        let moduleUrl = "\(self.address)modules/\(GlobalVar.moduleName)?token=\(self.token)"
        let method1: HTTPMethod = .get
        self.getRequest(urlString: moduleUrl, method: method1)
    }
    
    
    func alertHandler() {
        let alertControl = UIAlertController(title: "Module Options", message: "", preferredStyle: .alert)
        let confirm = UIAlertAction(title: "Confirm", style: .default) { (ACTION) in
            let method: HTTPMethod = .post
            var parameters = [String:Any]()
            for item in (alertControl.textFields)! {
                if item.text! != "" {
                    parameters[item.placeholder!] = item.text!
                }
            }
            let urlString = "\(self.address)modules/\(GlobalVar.moduleName)?token=\(self.token)"
            self.postRequest(urlString: urlString, method: method, parameters: parameters)
        }
        let cancel = UIAlertAction(title: "Cancel", style: .default) { (ACTION) in
        }
         for item in GlobalVar.json["modules"].arrayValue {
            for innerItem in item["options"] {
                alertControl.addTextField(configurationHandler: { (textField) in
                    textField.placeholder = innerItem.0
                    textField.keyboardType = .default
                    textField.borderStyle = .roundedRect
                })
            }
         }
        
        
        alertControl.addAction(confirm)
        alertControl.addAction(cancel)
        
        self.present(alertControl, animated: true, completion: nil)
    }
    
    @IBAction func backButtonPressed(_ sender: Any) {
        dismiss(animated: true)
    }
    
    
    func postRequest(urlString: String, method: HTTPMethod, parameters: [String:Any]) {
        self.group.enter()
        let URL: String = urlString
        let method: HTTPMethod = method
        let parameters = parameters
        let address = GlobalVar.IP
        
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
            self.dataHandler()
        }
    }
    
    func getRequest(urlString: String, method: HTTPMethod) {
        self.group.enter()
        let URL: String = urlString
        let method: HTTPMethod = method
        let address = GlobalVar.IP
        
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
            self.alertHandler()
        }
    }
    
    func dataHandler() {
        let json = GlobalVar.json
        print(json)
        let value = json["success"].stringValue
        if value == "true" {
            HUD.flash(.label("success"), delay: 2)
        } else {
            HUD.flash(.label("error"), delay: 4)
        }
    }
    
}
