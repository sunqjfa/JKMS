/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jyms.tools;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import jyms.data.TxtLogger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author John
 */
public class DomXML {
    
    private String sFileName = "--->>DomXML.java";
    private Document doc_obj = null;

    public DomXML(String pOutBuf, String FileName){
        sFileName = FileName + sFileName;
        doc_obj = readXMLString(pOutBuf);
    }

    /**
	 * 函数:      readXMLString
         * 函数描述:  读取系统输出的字符串，得到需要的DOM Document 对象
	 * @param pOutBuf	  系统输出的描述字符串，设备能力XML描述
         * @return Document    成功DOM Document 对象；失败返回null。.
     */
    private Document readXMLString(String pOutBuf){
            //得到DOM解析器的工厂实例
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = null;
            try {
                    // 从DOM工厂获得DOM解析器
                    db = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException pce) {
                    System.err.println(pce); 
                    return null;
            }

            Document doc = null;
            try {

                    //将给定输入源的内容解析为一个 XML 文档，并且返回一个新的 DOM Document 对象。
                    doc = db.parse(new InputSource(new StringReader(pOutBuf)));
                    // 对document对象调用normalize()，可以去掉xml文档中作为格式化内容的空白，
                    // 避免了这些空白映射在dom树中成为不必要的text node对象。
                    // 否则你得到的dom树可能并不是你所想象的那样。
                    // 特别是在输出的时候，这个normalize()更为有用。 
                    doc.normalize();
            } catch (DOMException | IOException | SAXException dom) {
                     TxtLogger.append(sFileName, "readXMLString()","系统在读取系统输出的字符串，得到需要的DOM Document 对象过程中，出现错误"
                             + "\r\n                       Exception:" + dom.toString());
                    return null;
            }
            return doc;
    }

    public  String readElementValue(String ElementName){
        return null;
    }
    /**
        * 函数:      readElementAttributeValue
        * 函数描述:  读取一级子节点的属性值
        * @param MajorNode 一级子节点
        * @param Attribute 一级子节点属性
        * @return String   一级子节点的属性值readSecondLevelAttributeValue
    */
    public  String readAttributeValue(String MajorNode, String Attribute){
        try{
            NodeList ElementList = doc_obj.getElementsByTagName(MajorNode);
            if (ElementList.getLength() == 1) {
                    Element e = (Element) ElementList.item(0);
                    // 取姓名元素的第一个子节点，即为姓名的值节点
                    return e.getAttribute(Attribute);
            }
        } catch (Exception e){
            TxtLogger.append(sFileName, "readAttributeValue()","系统在读取一级子节点的属性值过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return "";
    }
    /**
        * 函数:      isExistMajorNode
        * 函数描述:  判断是否存在该一级子节点
        * @param MajorNode 一级子节点
        * @return 是否存在该一级子节点
     */
    public boolean isExistMajorNode(String MajorNode){
        try{
            NodeList ElementList = doc_obj.getElementsByTagName(MajorNode);
            return ElementList.getLength()>0;
        } catch (Exception e){
            TxtLogger.append(sFileName, "isExistMajorNode()","判断是否存在该一级子节点过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return false;
    }
    /**
        * 函数:      readSecondLevelElementValue
        * 函数描述:  读取二级子节点的节点值
        * @param MajorNode 一级子节点
        * @param MinorNode 二级子节点
        * @return String    二级子节点的节点值
    */
    public String readSecondLevelElementValue(String MajorNode,String MinorNode){
        try{
            NodeList ElementList = doc_obj.getElementsByTagName(MajorNode);
            for (int i = 0; i < ElementList.getLength(); i++) {
                Element ENode = (Element) ElementList.item(i);
                NodeList Childs=ENode.getChildNodes();  
                for (int j=0;j<Childs.getLength();j++){
                    Node Child2 = Childs.item(j);
                    if (Child2.getNodeType() == Node.ELEMENT_NODE){
                        //二级节点
                        if (Child2.getNodeName().equals(MinorNode)){
                            //System.out.println(Child2.getFirstChild().getNodeValue());  
                            Text t = (Text) Child2.getFirstChild();
                            return t.getNodeValue();
                        }
                        
                    }
                }
//                NodeList names = ENode.getElementsByTagName(MinorNode);
//                            if (names.getLength() == 1) {
//                                    Element e = (Element) names.item(0);
//                                    Text t = (Text) e.getFirstChild();
//                                    // 取姓名元素的第一个子节点，即为姓名的值节点
//                                    return t.getNodeValue();
//                            }
            }
        } catch (Exception e){
            TxtLogger.append(sFileName, "readSecondLevelElementValue()","读取二级子节点的节点值过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return "";
    }
    /**
	 * 函数:      readSecondLevelAttributeValue
         * 函数描述:  读取二级子节点的属性值
         * @param MajorNode 一级子节点three-level; 
         * @param MinorNode 二级子节点
         * @param Attribute 二级子节点属性名称
         * @return String    属性值。.
         * <ExceptionAlarm>总是出问题。返回""
            <exceptionType opt="diskFull,diskError,nicBroken,ipConflict,illAccess"/>
            <alarmHandleType opt="center,alarmout,picture"/>
            <alarmHandleType
            <DetailedExceptionAlarm>
            <DiskFull>
            <alarmHandleType opt="center,alarmout,picture"/>
            </DiskFull>

            </ExceptionAlarm>

     */
    public String readSecondLevelAttributeValue(String MajorNode,String MinorNode,String Attribute){
        try {
//            Element root = doc.getDocumentElement();
            NodeList ElementList = doc_obj.getElementsByTagName(MajorNode);
            for (int i = 0; i < ElementList.getLength(); i++) {
                //一级节点
                Element ENode = (Element) ElementList.item(i);
                NodeList Childs=ENode.getChildNodes();  
                for (int j=0;j<Childs.getLength();j++){
                    Node Child2 = Childs.item(j);
                    if (Child2.getNodeType() == Node.ELEMENT_NODE){
                        //二级节点
                        if (Child2.getNodeName().equals(MinorNode)){
                            //System.out.println(Child2.getFirstChild().getNodeValue());  
                            return ((Element)Child2).getAttribute(Attribute);
                        }
                        
                    }
                }

                //以下的代码如果遇到二级节点和三级节点名字相同的话，就会出现问题。只能返回“”。
//                NodeList names = ENode.getElementsByTagName(MinorNode);
//			if (names.getLength() == 1) {
//				Element e = (Element) names.item(0);
//				// 取姓名元素的第一个子节点，即为姓名的值节点
//				return e.getAttribute(Attribute);
//			}
//                if (ENode.getTagName().equals(MinorNode)) return ENode.getAttribute(Attribute);
            }
        } catch (Exception e){
            TxtLogger.append(sFileName, "readSecondLevelAttributeValue()","读取二级子节点的属性值过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return "";
    }
    /**
        * 函数:      readSecondLevelAttributeValue
        * 函数描述:  读取三级子节点的属性值
        * @param MajorNodeName 一级子节点名称
        * @param MinorNodeName 二级子节点名称
        * @param ThirdNodeName 三级子节点名称
        * @return String    属性值。.
     */
    public String readThirdLevelElementValue(String MajorNodeName,String MinorNodeName,String ThirdNodeName){
        try {
//            Element root = doc.getDocumentElement();
            NodeList ElementList = doc_obj.getElementsByTagName(MajorNodeName);
            for (int i = 0; i < ElementList.getLength(); i++) {
                //一级节点
                Element MajorNode = (Element) ElementList.item(i);
                
                NodeList ChildsList=MajorNode.getChildNodes();  
                for (int j=0;j<ChildsList.getLength();j++){
                    Node Child2 = ChildsList.item(j);
                    
                    if (Child2.getNodeType() == Node.ELEMENT_NODE){
                        //二级节点
                        if (Child2.getNodeName().equals(MinorNodeName)){//找到所需要的二级节点
                            NodeList ChildsList2 = Child2.getChildNodes();
                            for (int k=0;k<ChildsList2.getLength();k++){
                                Node Child3 = ChildsList2.item(k);
                                if (Child3.getNodeType() == Node.ELEMENT_NODE){//三级节点
                                    //找到所需要的三级节点
                                    if(Child3.getNodeName().equals(ThirdNodeName)) {

                                        Text t = (Text) Child3.getFirstChild();
                                        return t.getNodeValue();
                                    }
                                }
                            }
                        }
                        
                    }
                }

            }
        } catch (Exception e){
            TxtLogger.append(sFileName, "readThirdLevelAttributeValue()","读取三级子节点的属性值过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return "";
    }
    
    /**
     * 函数:      readThirdLevelElementValueList(采用另外一种采集方法)
     * 函数描述:  读取多个相同次节点、多个不同三级子节点的值。例如（下面代码，需要得到resolutionName和index的列表）：
     *              <ManualCapture>
     *                  <ResolutionEntry><resolutionName>CIF</resolutionName><index>0</index></ResolutionEntry>
     *                  <ResolutionEntry><resolutionName>4CIF</resolutionName><index>2</index></ResolutionEntry>
     *                  <picQuality opt="best,better,normal" /> 
     *              </ManualCapture>
     * @param   MajorNodeName
     * @param   MinorNodeName
     * @param   ThirdNodeName[] 例如：{"resolutionName","index"}
     * @return  List    例如：{"CIF","0"}{"4CIF","2"}
     */
    public ArrayList readThirdLevelElementValueList(String MajorNodeName,String MinorNodeName,String[] ThirdNodeName){
        ArrayList NodeValueList = new ArrayList();
        try{
                //获得所有的ManualCapture节点
                NodeList ElementList = doc_obj.getElementsByTagName(MajorNodeName);//"ManualCapture"

                //查询所有的一级节点，即查询有多少个MajorNodeName
                for (int i = 0; i < ElementList.getLength(); i++) {
                    Node Node1 = ElementList.item(i);   //比如：ManualCapture
                    //Element Node1 = (Element) ElementList.item(i);

                    if (Node1.getNodeType() != Node.ELEMENT_NODE) continue;

                    NodeList NodeList2 = Node1.getChildNodes();
                    //查询所有的二级节点，即查询有多少个MinorNodeName
                    for (int j=0;j<NodeList2.getLength();j++){

                        Node Node2 = NodeList2.item(j); //比如：ResolutionEntry
                        if (Node2.getNodeType() != Node.ELEMENT_NODE) continue;

                        //每一个二级节点都应该建立一个ArrayList，用来存放所有需要的三级节点的值
                        ArrayList<String> NodeValueList3 = new ArrayList();

                        NodeList NodeList3 = Node2.getChildNodes();
                        //查询所有的三级节点，即查询有多少个ThirdNodeName
                        for (int k=0;k<NodeList3.getLength();k++){
                            Node Node3 = NodeList3.item(k); //比如：resolutionName和index
                            if (Node3.getNodeType() != Node.ELEMENT_NODE) continue;

                            for (String ss:ThirdNodeName){
                                if (ss.equals(Node3.getNodeName())){
                                    NodeValueList3.add(Node3.getTextContent());
                                }
                            }

                        }

                        //每一个二级节点在存放完所有需要的三级节点的值后，填入NodeValueList中保存
                        NodeValueList.add(NodeValueList3);

                    }

                }
                return NodeValueList;
        }catch(Exception ex){
            TxtLogger.append(sFileName, "readThirdLevelElementValueList()","读取多个相同次节点、多个不同三级子节点的值过程中，出现错误" + 
                         "\r\n                       Exception:" + ex.toString());  
        }
        return new ArrayList();
    }
    /**
        * 函数:      readSecondLevelAttributeValue
        * 函数描述:  读取三级子节点的属性值
        * @param MajorNodeName 一级子节点名称
        * @param MinorNodeName 二级子节点名称
        * @param ThirdNodeName 三级子节点名称
        * @param AttributeName 三级子节点属性名称
        * @return String    属性值。.
     */
    public String readThirdLevelAttributeValue(String MajorNodeName,String MinorNodeName,String ThirdNodeName,String AttributeName){
        try {
//            Element root = doc.getDocumentElement();
            NodeList ElementList = doc_obj.getElementsByTagName(MajorNodeName);
            for (int i = 0; i < ElementList.getLength(); i++) {
                //一级节点
                Element MajorNode = (Element) ElementList.item(i);
                
                NodeList ChildsList=MajorNode.getChildNodes();  
                for (int j=0;j<ChildsList.getLength();j++){
                    Node Child2 = ChildsList.item(j);
                    
                    if (Child2.getNodeType() == Node.ELEMENT_NODE){
                        //二级节点
                        if (Child2.getNodeName().equals(MinorNodeName)){//找到所需要的二级节点
                            NodeList ChildsList2 = Child2.getChildNodes();
                            for (int k=0;k<ChildsList2.getLength();k++){
                                Node Child3 = ChildsList2.item(k);
                                if (Child3.getNodeType() == Node.ELEMENT_NODE){//三级节点
                                    //找到所需要的三级节点
                                    if(Child3.getNodeName().equals(ThirdNodeName)) return ((Element)Child3).getAttribute(AttributeName);
                                }
                            }
                        }
                        
                    }
                }
//                NodeList MinorNodeList = MajorNode.getElementsByTagName(MinorNodeName);
//
//                for (int j=0;j<MinorNodeList.getLength();j++){
//                    Element MinorNode = (Element) MinorNodeList.item(i);
//                    NodeList ThirdNodeLis = MinorNode.getElementsByTagName(ThirdNodeName);
//                    if (ThirdNodeLis.getLength() == 1) {
//                        Element ThirdNode = (Element) ThirdNodeLis.item(0);
//                        return ThirdNode.getAttribute(AttributeName);
//                    }
//                }
//                if (ENode.getTagName().equals(MinorNode)) return ENode.getAttribute(Attribute);
            }
        } catch (Exception e){
            TxtLogger.append(sFileName, "readThirdLevelAttributeValue()","读取三级子节点的属性值过程中，出现错误" + 
                         "\r\n                       Exception:" + e.toString());  
        }
        return "";
    }
//    /**
//	 * 函数:      readXMLFile
//         * 函数描述:  读取系统输出的字符串，得到需要的元素值 SecondLevel
//	 * @param pOutBuf	  系统输出的描述字符串，能力XML描述
//         * @param MajorNode	主节点名
//         * @param ListMinorNode MinorNode次节点列表
//         * @param ListAttribute 每一个次节点相对应一个Attribute属性列表
//         * @return boolean：成功true；不具有或者失败返回false。.
//     */
//    public static ArrayList<String> readXMLFile(String pOutBuf,String MajorNode,ArrayList<String> ListMinorNode,ArrayList<ArrayList> ListAttribute) throws Exception {
//		//	得到DOM解析器的工厂实例
//		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//		DocumentBuilder db = null;
//		try {
//			// 从DOM工厂获得DOM解析器
//			db = dbf.newDocumentBuilder();
//		} catch (ParserConfigurationException pce) {
//			System.err.println(pce); 
//			return null;
//		}
//
//		Document doc = null;
//		try {
//
//                        //将给定输入源的内容解析为一个 XML 文档，并且返回一个新的 DOM Document 对象。
//			doc = db.parse(new InputSource(new StringReader(pOutBuf)));
//			// 对document对象调用normalize()，可以去掉xml文档中作为格式化内容的空白，
//			// 避免了这些空白映射在dom树中成为不必要的text node对象。
//			// 否则你得到的dom树可能并不是你所想象的那样。
//			// 特别是在输出的时候，这个normalize()更为有用。 
//			doc.normalize();
//		} catch (DOMException dom) {
//			System.err.println(dom.getMessage());
//			return null;
//		} catch (IOException ioe) {
//			System.err.println(ioe);
//			return null;
//		}
//
//		List studentBeans = new ArrayList();
//		StudentBean studentBean = null;
//		//	得到XML文档的根节点“学生花名册”
//		Element root = doc.getDocumentElement();
//		//	取"学生"元素列表
//		NodeList students = root.getElementsByTagName("学生");
//		for (int i = 0; i < students.getLength(); i++) {
//			//	依次取每个"学生"元素
//			Element student = (Element) students.item(i);
//			//	创建一个学生的Bean实例
//			studentBean = new StudentBean();
//			//	取学生的性别属性
//			studentBean.setGender(student.getAttribute("性别"));
//			
//			//	取“姓名”元素
//			NodeList names = student.getElementsByTagName("姓名");
//			if (names.getLength() == 1) {
//				Element e = (Element) names.item(0);
//				// 取姓名元素的第一个子节点，即为姓名的值节点
//				Text t = (Text) e.getFirstChild();
//				// 获取值节点的值
//				studentBean.setName(t.getNodeValue());
//			}
//
//			// 取“年龄”元素
//			NodeList ages = student.getElementsByTagName("年龄");
//			if (ages.getLength() == 1) {
//				Element e = (Element) ages.item(0);
//				Text t = (Text) e.getFirstChild();
//				studentBean.setAge(Integer.parseInt(t.getNodeValue()));
//			}
//
//			//	取“电话”元素
//			NodeList phones = student.getElementsByTagName("电话");
//			if (phones.getLength() == 1) {
//				Element e = (Element) phones.item(0);
//				Text t = (Text) e.getFirstChild();
//				studentBean.setPhone(t.getNodeValue());
//			}
//			// 将新建的Bean加到结果列表中
//			studentBeans.add(studentBean);
//		}
//		// 返回结果列表
//		return studentBeans;
//	}
    public static void main(String[] args){
        String xmls ="<JpegCaptureAbility version=\"2.0\">  <channelNO>1</channelNO> <FindPicInfo>  <supportFileType opt=\"CMR,MOTION,ALARM,EDR,ALARMANDMOTION,manual,intelligentPic,pir,wlsensor,callhelp, previewScreenshot,facedetection,LineDetection,FieldDetection,scenechangedetection, lockPlaybackScreenshot,INTELLIGENT,regionEntrance,regionExiting,loitering,group, rapidMove,parking,unattendedBaggage,attendedBaggage,VehicleDetection,HvtVehicleDetection, evidence,fireDetection,allType\" />   <enableNeedCard opt=\"disable,able\" />   <province opt=\"1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,26,27,28,29,30,31,32,33,34,0xff\" />   <cardNumberLen min=\"\" max=\"\" /> <StartTime>  <year min=\"\" max=\"\" />   <month min=\"\" max=\"\" />   <day min=\"\" max=\"\" />   <hour min=\"\" max=\"\" />   <minute min=\"\" max=\"\" />   <second min=\"\" max=\"\" />   </StartTime><StopTime>  <year min=\"\" max=\"\" />   <month min=\"\" max=\"\" />   <day min=\"\" max=\"\" />   <hour min=\"\" max=\"\" />   <minute min=\"\" max=\"\" />   <second min=\"\" max=\"\" />   </StopTime>  <trafficType opt=\"license,vehicleType,illegalType\" />   <vehicleType opt=\"smallCar,bigCar,bus,truck,car,minibus,smallTruck\" />   <subHvtType opt=\"all,motorVehicle,nonMotorVehicle,pedestrian\" />   <illegalType opt=\"postPic,lowSpeed,highSpeed,retrograde,rushRedLight,pressLane,violateGuide,roadStrand, vehicleillegal,roadStand,changeLane,dirveillegalLane,violate,crossParking,greenParking\" />   <region opt=\"Res,EU,ER,EUAndCIS,All\" />   <country opt=\"0,1,2,3,4,5,6,7,8,9,10,11,12,0xfe,0xff\" />   <licenseLen min=\"\" max=\"\" />   </FindPicInfo><SmartPicSearchInfo>  <supportFileType opt=\"vehicleDetection,faceFeature,facePicData,FieldDetection,unattendedBaggage,attendedBaggage,regionEntrance, regionExiting,parking,loitering,group,rapidMove,allType\" />  <StartTime>  <year min=\"\" max=\"\" />   <month min=\"\" max=\"\" />   <day min=\"\" max=\"\" />   <hour min=\"\" max=\"\" />   <minute min=\"\" max=\"\" />   <second min=\"\" max=\"\" />   </StartTime> <StopTime>  <year min=\"\" max=\"\" />   <month min=\"\" max=\"\" />   <day min=\"\" max=\"\" />   <hour min=\"\" max=\"\" />   <minute min=\"\" max=\"\" />   <second min=\"\" max=\"\" />   </StopTime><VehicleCond>   <licenseLen min=\"1\" max=\"16\" />   <country opt=\"czech,france,germany,spain,italy,netherlands,poland,slovakia, belorussia,moldova,russia,ukraine,\" />   </VehicleCond><FaceFeature>  <ageGroup opt=\"infant,child,youngster,adolescent,youth,prime,midlife,midage,old\" />   <sex opt=\"man,women\" />   <withGlasses opt=\"true,false\" />   </FaceFeature><FacePicData>  <faceScore min=\"0\" max=\"100\" />   <picType opt=\"jpg\" />   </FacePicData>  </SmartPicSearchInfo> <ManualCapture><ResolutionEntry>  <resolutionName>CIF</resolutionName>   <index>0</index>   </ResolutionEntry><ResolutionEntry>   <resolutionName>4CIF</resolutionName>   <index>2</index>   </ResolutionEntry>  <picQuality opt=\"best,better,normal\" />   </ManualCapture> <SchedCapture> <TimingCap> <ResolutionEntry>  <resolutionName>CIF</resolutionName>   <index>0</index>   </ResolutionEntry><ResolutionEntry>  <resolutionName>4CIF</resolutionName>   <index>2</index>   </ResolutionEntry>  <intervalUnit>ms</intervalUnit>   <interval min=\"\" max=\"\" opt=\"0\" />   <RecorderDuration min=\"\" max=\"\" />  <DayCapture>  <captureType opt=\"timing,motion,alarm,motionOrAlarm,motionAndAlarm,vca,command\" />    </DayCapture> <TimeSlot>  <slotNum>8</slotNum>   <captureType opt=\"timing,motion,alarm,motionOrAlarm,motionAndAlarm,vca,command\" />   </TimeSlot><HolidayDay>  <captureType opt=\"timing,motion,alarm,motionOrAlarm,motionAndAlarm,vca,command\" />   </HolidayDay><HolidayTimeSlot>  <slotNum>8</slotNum>   <captureType opt=\"timing,motion,alarm,motionOrAlarm,motionAndAlarm,vca,command\" />   </HolidayTimeSlot>  </TimingCap><EventCap>  <eventType opt=\"motion,hide,loss,PIR,wireless,callhelp,vca,facedDetect,lineDetection, filedDetection,sceneChangeDetection,regionEntrance,regionExiting,loitering, group,rapidMove,parking,unattendedBaggage,attendedBaggage\" />  <ResolutionEntry>   <resolutionName>CIF</resolutionName>   <index>0</index>   </ResolutionEntry> <ResolutionEntry>  <resolutionName>4CIF</resolutionName>   <index>2</index>   </ResolutionEntry>  <intervalUnit>ms</intervalUnit>   <interval min=\"\" max=\"\" opt=\"0\" />   <capTimes min=\"\" max=\"\" />   <eventCapChan opt=\"1,2\" />   <alarmInCapChan opt=\"1,2\" />   </EventCap> <AdvancedParam>  <streamType opt=\"0-mainstream,1-substream\">   </streamType>  </AdvancedParam>  </SchedCapture>  </JpegCaptureAbility>";
        DomXML domXML = new DomXML(xmls,"self");
        
        ArrayList ss = domXML.readThirdLevelElementValueList("ManualCapture","ResolutionEntry",new String[]{"resolutionName","index"});
        
        for (int i=0;i<ss.size();i++){
            ArrayList aa = (ArrayList)ss.get(i);
            for (int j=0;j<aa.size();j++){
                System.out.println(aa.get(j));
            }
            
        }
        System.out.println("value: " +ss);//ManualCapture
        
        //获得所有的ManualCapture节点
        NodeList ElementList = domXML.doc_obj.getElementsByTagName("ManualCapture");

        int num = ElementList.getLength();
        for (int i = 0; i < num; i++) {
            Node Node1 = ElementList.item(i);
            //Element Node1 = (Element) ElementList.item(i);
            System.out.println("首节点: " + Node1.getNodeName());//ManualCapture
            if (Node1.getNodeType() != Node.ELEMENT_NODE) continue;
            
            NodeList NodeList2 = Node1.getChildNodes();
            int num2 =NodeList2.getLength();
            for (int j=0;j<num2;j++){
                Node Node2 = NodeList2.item(j);
                
                if (Node2.getNodeType() != Node.ELEMENT_NODE) continue;
                
                System.out.println("次节点: " + Node2.getNodeName());//ResolutionEntry
                //if (!("ResolutionEntry".equals(Node2.getNodeName()))) continue;
                
                
                NodeList NodeList3 = Node2.getChildNodes();
                int num3 = NodeList3.getLength();
                for (int k=0;k<num3;k++){
                    Node Node3 = NodeList3.item(k);
                    if (Node3.getNodeType() != Node.ELEMENT_NODE) continue;
                    
                    System.out.println("三级节点: " + Node3.getNodeName());//ResolutionEntry
                    if ("resolutionName".equals(Node3.getNodeName())){
                        System.out.println("分辨率名称: " + Node3.getTextContent());
                    }else if ("index".equals(Node3.getNodeName())){
                        System.out.println("该分辨率对应SDK中定义的值: " + Node3.getTextContent());
                    }
                }
                
            }

        }
    }
}
