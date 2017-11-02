package common;


import java.net.InetAddress;
import java.net.UnknownHostException;

public class OSUtil {
	/**   
	 * @Function getHostNameForLiunx
	 * @Description 获取计算机名win-linux都可以
	 * @return
	 * @version v1.0.0
	 * @author dl
	 * @date 2017年4月26日 下午2:54:10
	 * <strong>Modification History:</strong><br>
	 * Date         Author          Version            Description<br>
	 * ---------------------------------------------------------<br>
	 * 2017年4月26日     dl           v1.0.0            修改原因<br>
	 */
	public static String getHostNameForLiunx() {  
        try {  
            return (InetAddress.getLocalHost()).getHostName();  
        } catch (UnknownHostException uhe) {  
            String host = uhe.getMessage(); // host = "hostname: hostname"  
            if (host != null) {  
                int colon = host.indexOf(':');  
                if (colon > 0) {  
                    return host.substring(0, colon);  
                }  
            }  
            return "UnknownHost";  
        }  
    }  
}
