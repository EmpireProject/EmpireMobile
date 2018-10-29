
import UIKit
import Alamofire
import SwiftyJSON
import PKHUD

protocol settingLabels {
    func setLabels()
}

class AgentsChildViewController: UIViewController {

    @IBOutlet var stackView: UIStackView!
    @IBOutlet var getAgentsButton: UIButton!
    
    var delegate: settingLabels?
    
    let address = GlobalVar.serverAddr
    let token = GlobalVar.token
    let group = DispatchGroup()
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    @IBAction func getAgentsButton(_ sender: Any) {
        for view in self.stackView.arrangedSubviews {
            if view != self.getAgentsButton {
                view.removeFromSuperview()
            }
        }
        let urlString = "\(address)agents?token=\(token)"
        let method: HTTPMethod = .get
        let funcName: String = #function
        getRequest(urlString: urlString, method: method, funcName: funcName)
    }
    
    @objc func buttonAction(sender: UIButton!) {
        let button = sender
        let name: String = button!.currentTitle!
        let urlString = "\(address)agents/\(name)?token=\(token)"
        let method: HTTPMethod = .get
        let funcName: String = #function
        getRequest(urlString: urlString, method: method, funcName: funcName)
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
    
    func parseData() {
        let dataName = GlobalVar.funcName
        let json = GlobalVar.json
        switch dataName {
        case "getAgentsButton":
            if json["agents"].count >= 1 {
                for item in json["agents"].arrayValue {
                    let button = UIButton()
                    button.backgroundColor = UIColor.clear
                    button.heightAnchor.constraint(equalToConstant: 34).isActive = true
                    button.widthAnchor.constraint(equalToConstant: 140).isActive = true
                    button.setTitle(item["name"].stringValue, for: .normal)
                    button.addTarget(self, action: #selector(buttonAction), for: .touchUpInside)
                    self.stackView.addArrangedSubview(button)
                }
            }
            
        case "staleAgents":
            var staleNames = [String]()
            let firstElement = json["agents"].arrayValue
            for item in firstElement {
                staleNames.append(item["name"].stringValue)
            }
            GlobalVar.staleNames = staleNames
            delegate?.setLabels()
            
        case "buttonAction(sender:)":
            let firstElement = json["agents"].arrayValue
            GlobalVar.agentName = firstElement[0]["name"].stringValue
            GlobalVar.hostname = firstElement[0]["hostname"].stringValue
            GlobalVar.procID = firstElement[0]["process_id"].stringValue
            GlobalVar.extIP = firstElement[0]["external_ip"].stringValue
            GlobalVar.internalIP = firstElement[0]["internal_ip"].stringValue
            GlobalVar.listener = firstElement[0]["listener"].stringValue
            GlobalVar.procName = firstElement[0]["process_name"].stringValue
            GlobalVar.username = firstElement[0]["username"].stringValue
            GlobalVar.lastseen = firstElement[0]["lastseen_time"].stringValue
            GlobalVar.osDetails = firstElement[0]["os_details"].stringValue
            GlobalVar.checkinTime = firstElement[0]["checkin_time"].stringValue
            GlobalVar.delay = firstElement[0]["delay"].stringValue
            let staleName: String = "staleAgents"
            let urlString2 = "\(address)agents/stale?token=\(token)"
            let method: HTTPMethod = .get
            getRequest(urlString: urlString2, method: method, funcName: staleName)
            
        default:
            print("something went wrong!")
        }
    }
}
