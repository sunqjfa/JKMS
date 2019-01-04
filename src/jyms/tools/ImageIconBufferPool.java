
package jyms.tools;

import java.net.URL;
import java.util.HashMap;
import javax.swing.ImageIcon;
import jyms.data.TxtLogger;

/**
 *
 * @author John
 */
public class ImageIconBufferPool {
    
    private final static String IMAGE_PATH = "/jyms/image/";

    private HashMap<String,ImageIcon> bufferPool = new HashMap<>();
    private Class baseClass = this.getClass();
    
    private static ImageIconBufferPool instance = null;
    
    public static ImageIconBufferPool getInstance(){
        if(instance==null)
            instance = new ImageIconBufferPool();
        return instance;
    }
    
    public ImageIcon getImageIcon(String ImageName){
            ImageIcon Image2 = null;
            URL Url = baseClass.getResource(IMAGE_PATH + ImageName);
            String Key = Url.toString();
            if(bufferPool.containsKey(Key)){
                Image2 = bufferPool.get(Key);
            }else{
                try{
                        Image2 = new ImageIcon(Url);
                        bufferPool.put(Key, Image2);
                }catch (Exception e){
                    TxtLogger.append(baseClass.getName(), "getImageIcon()","系统在登录过程中，出现错误"
                                   + "\r\n                       取本地磁盘资源文件出错,path=“"+Key+"”\r\n"
                                   + e.toString());
                }
            }
            return Image2;
    }
}
