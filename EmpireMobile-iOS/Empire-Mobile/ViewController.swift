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
import Foundation
import CommonCrypto

class ViewController: UIViewController, UITextFieldDelegate {

    
   
    @IBOutlet weak var authButton: UIButton!
    @IBOutlet weak var empireImage: UIImageView!
    @IBOutlet weak var addressTextField: UITextField!
    @IBOutlet weak var userTextField: UITextField!
    @IBOutlet weak var passTextField: UITextField!
    @IBOutlet weak var IPErrorLabel: UILabel!
    @IBOutlet weak var UserErrorLabel: UILabel!
    @IBOutlet weak var saveSwitch: UISwitch!
    
    
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
        
        do {
            try getData()
            
        } catch {
            print("Error getting Creds!")
        }
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
            if (saveSwitch != nil) {
                
            }
            
            setData()
            
            let addysplit = address.components(separatedBy: ":")
            let addy = addysplit[0]
            GlobalVar.IP = addy
            let urlString: String = "https://\(address!)/api/"
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
    
    var secretKey: String = ""
    var IV: String = ""
    var creds1: String = ""
    let service = "EmpireService"
    let account = "EmpireAccount"
    
    // Arguments for the keychain queries
    let kSecClassValue = NSString(format: kSecClass)
    let kSecAttrAccountValue = NSString(format: kSecAttrAccount)
    let kSecValueDataValue = NSString(format: kSecValueData)
    let kSecClassGenericPasswordValue = NSString(format: kSecClassGenericPassword)
    let kSecAttrServiceValue = NSString(format: kSecAttrService)
    let kSecMatchLimitValue = NSString(format: kSecMatchLimit)
    let kSecReturnDataValue = NSString(format: kSecReturnData)
    let kSecMatchLimitOneValue = NSString(format: kSecMatchLimitOne)
    
    func setData() {
        let credDict = ["username": userTextField.text, "password": passTextField.text, "address": addressTextField.text]
        var encDict = ["": ""]
        
        for (key, value) in credDict {
            var encryptedBlob: Data? = nil
            do {
                encryptedBlob = try storeCreds(data: value!)
            } catch {
            }
            let cryptedString = encryptedBlob?.base64EncodedString()
            let storedString = IV + cryptedString!
            encDict[MD5(key)!] = storedString
        }
        
        do {
            try savePropertyList(encDict)
        } catch {
            print(error)
        }
    }
    
    func getData() throws {
        var credDict = ["": ""]
        do {
            var dictionary = try loadPropertyList()
            if let cred = dictionary[MD5("password")!] ?? nil, let cred2 = dictionary[MD5("address")!] ?? nil, let cred3 = dictionary[MD5("username")!] ?? nil {
                credDict["pass"] = cred
                credDict["addy"] = cred2
                credDict["user"] = cred3
            } else {
                print("Error somewhere in here")
                return
            }
        }
        
        let keychainQuery: NSMutableDictionary = NSMutableDictionary(objects: [kSecClassGenericPasswordValue, service, account, kCFBooleanTrue, kSecMatchLimitOneValue], forKeys: [kSecClassValue, kSecAttrServiceValue, kSecAttrAccountValue, kSecReturnDataValue, kSecMatchLimitValue])
        var dataTypeRef :AnyObject?
        // Search for the keychain items
        let status: OSStatus = SecItemCopyMatching(keychainQuery, &dataTypeRef)
        var contentsOfKeychain: String?
        if status == errSecSuccess {
            if let retrievedData = dataTypeRef as? Data {
                contentsOfKeychain = String(data: retrievedData, encoding: String.Encoding.utf8)
            }
        } else {
            print("Nothing was retrieved from the keychain. Status code \(status)")
        }
        
        let decKey = contentsOfKeychain
        let startIndex = String.Index(encodedOffset: 0)
        let endIndex = String.Index(encodedOffset: 16)
        var decryptedCreds = ["": ""]
        
        for (key, value) in credDict {
            if !key.isEmpty {
                let full = String.Index(encodedOffset: value.count)
                
                IV = String(value[startIndex..<endIndex])
                let encData = String(value[endIndex..<full])
                
                let decodedString = fromBase64(coffee: encData)
                let plainText = decryptCreds(data: decodedString!, key: decKey!, iv: IV)
                decryptedCreds[key] = plainText
            } else {}
        }
        passTextField.text = decryptedCreds["pass"]
        userTextField.text = decryptedCreds["user"]
        addressTextField.text = decryptedCreds["addy"]
    }
    
    func decryptCreds(data: Data, key: String, iv: String) -> String {
        let aes256 = AES(key: key, iv: iv)
        let decryptStuff = aes256?.decrypt(data: data)
        return decryptStuff!
    }
    
    func keyGen(length: Int = 16) -> String {
        let allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var key: String = ""
        
        for _ in 0..<length {
            let random = arc4random_uniform(UInt32(allowedChars.count))
            key += "\(allowedChars[allowedChars.index(allowedChars.startIndex, offsetBy: Int(random))])"
        }
        return key
    }
    
    func genIV(length: Int = 16) -> String {
        let allowedChars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        var IV: String = ""
        
        for _ in 0..<length {
            let random = arc4random_uniform(UInt32(allowedChars.count))
            IV += "\(allowedChars[allowedChars.index(allowedChars.startIndex, offsetBy: Int(random))])"
        }
        return IV
    }
    
    enum CustomError: Error {
        case errorSavingKey
        case errorGettingKey
    }
    
    func storeCreds(data: String) throws -> Data {
        if secretKey.isEmpty {
            secretKey = keyGen()
        } else {
        }
        
        let keyData = secretKey.data(using: .utf8, allowLossyConversion: false)!
        
        let keychainQuery: NSMutableDictionary = NSMutableDictionary(objects: [kSecClassGenericPasswordValue, service, account, kCFBooleanTrue, kSecMatchLimitOneValue], forKeys: [kSecClassValue, kSecAttrServiceValue, kSecAttrAccountValue, kSecReturnDataValue, kSecMatchLimitValue])
        var dataTypeRef :AnyObject?
        // Search for the keychain items
        let getStatus: OSStatus = SecItemCopyMatching(keychainQuery, &dataTypeRef)
        //var contentsOfKeychain: String?
        if getStatus == errSecSuccess {
            let addquery: NSMutableDictionary = NSMutableDictionary(objects: [kSecClassGenericPasswordValue, service, account, keyData], forKeys: [kSecClassValue, kSecAttrServiceValue, kSecAttrAccountValue, kSecValueDataValue])
            let status = SecItemUpdate(addquery as CFDictionary, [kSecValueDataValue:keyData] as CFDictionary)
            guard status == errSecSuccess else {
                if let err = SecCopyErrorMessageString(status, nil) {
                    print("Write Failed: " + (err as String))
                }
                throw CustomError.errorSavingKey
            }
        } else {
            let addquery: NSMutableDictionary = NSMutableDictionary(objects: [kSecClassGenericPasswordValue, service, account, keyData], forKeys: [kSecClassValue, kSecAttrServiceValue, kSecAttrAccountValue, kSecValueDataValue])
            let status = SecItemAdd(addquery as CFDictionary, nil)
            guard status == errSecSuccess else {
                if let err = SecCopyErrorMessageString(status, nil) {
                    print("Write Failed: " + (err as String))
                }
                throw CustomError.errorSavingKey
            }
        }
    
        if IV.isEmpty {
            IV = genIV()
        } else {
        }
        
        let aes256 = AES(key: secretKey, iv: IV)
        let encryptedPassword = aes256?.encrypt(string: data)
        return encryptedPassword!
    }
    
    struct AES {
        
        var key: Data
        var iv: Data
        
        //Initialzier
        init?(key: String, iv: String) {
            guard key.count == kCCKeySizeAES128 || key.count == kCCKeySizeAES256, let keyData = key.data(using: .utf8) else {
                debugPrint("Error: Failed to set a key.")
                return nil
            }
            
            guard iv.count == kCCBlockSizeAES128, let ivData = iv.data(using: .utf8) else {
                debugPrint("Error: Failed to set an initial vector.")
                return nil
            }
            
            self.key = keyData
            self.iv  = ivData
        }
        
        func encrypt(string: String) -> Data? {
            return crypt(data: string.data(using: .utf8), option: CCOperation(kCCEncrypt))
        }
        
        func decrypt(data: Data?) -> String? {
            guard let decryptedData = crypt(data: data, option: CCOperation(kCCDecrypt)) else { return nil }
            return String(bytes: decryptedData, encoding: .utf8)
        }
        
        func crypt(data: Data?, option: CCOperation) -> Data? {
            guard let data = data else { return nil }
            
            let cryptLength = [UInt8](repeating: 0, count: data.count + kCCBlockSizeAES128).count
            var cryptData   = Data(count: cryptLength)
            
            let keyLength = [UInt8](repeating: 0, count: kCCBlockSizeAES128).count
            let options   = CCOptions(kCCOptionPKCS7Padding)
            
            var bytesLength = Int(0)
            
            let status = cryptData.withUnsafeMutableBytes { cryptBytes in
                data.withUnsafeBytes { dataBytes in
                    iv.withUnsafeBytes { ivBytes in
                        key.withUnsafeBytes { keyBytes in
                            CCCrypt(option, CCAlgorithm(kCCAlgorithmAES), options, keyBytes, keyLength, ivBytes, dataBytes, data.count, cryptBytes, cryptLength, &bytesLength)
                        }
                    }
                }
            }
            
            guard UInt32(status) == UInt32(kCCSuccess) else {
                debugPrint("Error: Failed to crypt data. Status \(status)")
                return nil
            }
            
            cryptData.removeSubrange(bytesLength..<cryptData.count)
            return cryptData
        }
    }
    
    func fromBase64(coffee: String) -> Data? {
        guard let data = NSData(base64Encoded: coffee, options: NSData.Base64DecodingOptions(rawValue: 0)) as Data? else {
            return nil
        }
        return data
    }
    
    var plistURL : URL {
        let documentDirectoryURL =  try! FileManager.default.url(for: .documentDirectory, in: .userDomainMask, appropriateFor: nil, create: false)
        return documentDirectoryURL.appendingPathComponent("Empire.plist")
    }
    
    func savePropertyList(_ plist: Any) throws
    {
        let plistData = try PropertyListSerialization.data(fromPropertyList: plist, format: .xml, options: 0)
        try plistData.write(to: plistURL)
    }
    
    
    func loadPropertyList() throws -> [String:String]
    {
        let data = try Data(contentsOf: plistURL)
        guard let plist = try PropertyListSerialization.propertyList(from: data, format: nil) as? [String:String] else {
            return [String:String]()
        }
        return plist
    }
    
    func MD5(_ string: String) -> String? {
        let length = Int(CC_MD5_DIGEST_LENGTH)
        var digest = [UInt8](repeating: 0, count: length)
        
        if let d = string.data(using: String.Encoding.utf8) {
            _ = d.withUnsafeBytes { (body: UnsafePointer<UInt8>) in
                CC_MD5(body, CC_LONG(d.count), &digest)
            }
        }
        
        return (0..<length).reduce("") {
            $0 + String(format: "%02x", digest[$1])
        }
    }
}

