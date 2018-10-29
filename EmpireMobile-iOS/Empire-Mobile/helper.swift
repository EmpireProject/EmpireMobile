
import Foundation
import PKHUD
import Alamofire
import SwiftyJSON

class Helper{
    static let regex = try? NSRegularExpression(pattern: "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\." + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])" + ":" + "[0-9]{1,5}$", options: .caseInsensitive)
    static let userIllegalChars: [String] = ["\\","/","'","{","}","<",">","%","(",")",";",":","~","&","[","]","|","+","-"]
    
    static func validateIP(inputString: String) -> Bool {
        let results = regex?.matches(in: inputString, options: [], range: NSRange(inputString.startIndex..., in: inputString))
        if (results?.description.contains("[]"))! {
            return false
        } else {
            return true
        }
    }
    
    static func validateUser(inputString: String) -> Bool {
        for item in userIllegalChars {
            if inputString.contains(item) {
                return false
            }
        }
        return true
    }
}

struct GlobalVar{
    static var token: String = ""
    static var serverAddr: String = ""
    static var IP: String = ""
    static var results: [String:String] = ["":""]
    static var json: JSON = [:]
    static var funcName: String = ""
    static var agentName: String = ""
    static var hostname: String = ""
    static var procID: String = ""
    static var extIP: String = ""
    static var internalIP: String = ""
    static var listener: String = ""
    static var procName: String = ""
    static var username: String = ""
    static var lastseen: String = ""
    static var osDetails: String = ""
    static var checkinTime: String = ""
    static var delay: String = ""
    static var popupLabel: String = ""
    static var staleNames = [String]()
    static var powershell = [String]()
    static var python = [String]()
    static var exfil = [String]()
    static var external = [String]()
    static var modulesType: String = ""
    static var moduleName: String = ""
}
