//
//  ViewController.swift
//  Empire-Mobile
//
//  Created by Shannon Lucas on 4/30/18.
//  Copyright © 2018 Pickles. All rights reserved.
//

import UIKit
import PKHUD
import Alamofire
import SwiftyJSON

class ViewController: UIViewController, UITextFieldDelegate {

    
   
    @IBOutlet weak var authButton: UIButton!
    @IBOutlet weak var empireImage: UIImageView!
    @IBOutlet weak var addressTextField: UITextField!
    @IBOutlet weak var userTextField: UITextField!
    @IBOutlet weak var passTextField: UITextField!
    @IBOutlet weak var IPErrorLabel: UILabel!
    @IBOutlet weak var UserErrorLabel: UILabel!
    
    
    var activeTextField : UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        let center: NotificationCenter = NotificationCenter.default
        center.addObserver(self, selector: #selector(keyboardDidShow(notification:)),
            name: NSNotification.Name.UIKeyboardWillShow, object: nil)
        center.addObserver(self, selector: #selector(keyboardWillHide(notification:)),
                           name: NSNotification.Name.UIKeyboardWillHide, object: nil)
        
        passTextField.delegate = self
        userTextField.delegate = self
        addressTextField.delegate = self
        
        let notificationCenter = NotificationCenter.default
        notificationCenter.addObserver(self, selector: #selector(appMovedToBackground), name: NSNotification.Name.UIApplicationWillResignActive, object: nil)
        
        enum LoginError: Error {
            case incorrectCredentials
        }
        IPErrorLabel.isHidden = true
        UserErrorLabel.isHidden = true
    }
    
    @objc func appMovedToBackground() {
        let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
        let newViewController = storyBoard.instantiateViewController(withIdentifier: "splashScene") as UIViewController
        self.present(newViewController, animated: true, completion: nil)
    }
    
    @objc func keyboardDidShow(notification: Notification) {
        let info:NSDictionary = notification.userInfo! as NSDictionary
        let keyboardSize = (info[UIKeyboardFrameEndUserInfoKey] as! NSValue).cgRectValue.size
        let keyboardY = self.view.frame.size.height - keyboardSize.height
        
        let editingTextFieldY:CGFloat! = self.activeTextField?.frame.origin.y
        
        if self.view.frame.origin.y >= 0 {
            //Checking if the textfield is really hidden behind the keyboard
            if editingTextFieldY > keyboardY - 60 {
                UIView.animate(withDuration: 0.25, delay: 0.0, options: UIViewAnimationOptions.curveEaseIn, animations: {
                    self.view.frame = CGRect(x: 0, y: self.view.frame.origin.y - (editingTextFieldY! - (keyboardY - 60)), width: self.view.bounds.width,height: self.view.bounds.height)
                }, completion: nil)
            }
        }
    }
    
    @objc func keyboardWillHide(notification: Notification) {
        UIView.animate(withDuration: 0.25, delay: 0.0, options: UIViewAnimationOptions.curveEaseIn, animations: {
            self.view.frame = CGRect(x: 0, y: 0,width: self.view.bounds.width, height: self.view.bounds.height)
        }, completion: nil)
    };
    
    func textFieldDidBeginEditing(_ textField: UITextField) {
        activeTextField = textField
    }
    
    func textFieldShouldReturn(_ textField: UITextField) -> Bool {
        textField.resignFirstResponder()
        return true
    }
    
    override func viewWillDisappear(_ animated: Bool) {
        NotificationCenter.default.removeObserver(self, name: Notification.Name.UIKeyboardWillShow, object: nil)
        NotificationCenter.default.removeObserver(self, name: Notification.Name.UIKeyboardWillHide, object: nil)
    }
    
    @IBAction func authBtnPressed(_ sender: Any) {
        
        let username: String! = userTextField.text
        let validUser: Bool = Helper.validateUser(inputString: username)
        if !validUser {
            UserErrorLabel.isHidden = false
        } else {
            UserErrorLabel.isHidden = true
        }
        let password: String! = passTextField.text
        let address: String! = addressTextField.text
        let validated: Bool = Helper.validateIP(inputString: address)
        if !validated {
            IPErrorLabel.isHidden = false
        } else {
            IPErrorLabel.isHidden = true
            let addysplit = address.components(separatedBy: ":")
            let addy = addysplit[0]
            GlobalVar.IP = addy
            let urlString: String = "https://" + address! + "/api/"
            let loginURL: String = urlString + "admin/login"
            GlobalVar.serverAddr = urlString
            let meth: HTTPMethod = HTTPMethod.post
            var results: [String:String] = ["":""]
            
            let parameters: [String:Any] = ["username": username, "password": password]
            
            let serverTrustPolicies: [String: ServerTrustPolicy] = [addy: .disableEvaluation]
            
            let sessionManager = SessionManager(
                serverTrustPolicyManager: ServerTrustPolicyManager(policies: serverTrustPolicies)
            )
            
            let group = DispatchGroup()
            group.enter()
            sessionManager.request(loginURL, method: meth, parameters: parameters, encoding: JSONEncoding.default, headers: ["Accept": "application/json"])
                .responseJSON { response in
                    sessionManager.session.invalidateAndCancel()
                    if let result = response.result.value {
                        let JSON = result as! [String:String]
                        results = JSON
                    }
                    group.leave()
            }
            group.notify(queue: DispatchQueue.main) {
                do {
                    guard let token: String = try results["token"] ?? nil else {
                        HUD.flash(.label("Wrong address, username, or password!"), delay: 2.0)
                        return
                    }
                    GlobalVar.token = token
                    HUD.flash(.label("Authentication Successful!"), delay: 2.0)
                    let storyBoard: UIStoryboard = UIStoryboard(name: "Main", bundle: nil)
                    let newViewController = storyBoard.instantiateViewController(withIdentifier: "PostLoginViewController") as UIViewController
                    self.present(newViewController, animated: true, completion: nil)
                } catch {
                    print("Ooops, something went wrong! \(error)")
                }
            }
        }
        
    }
}

