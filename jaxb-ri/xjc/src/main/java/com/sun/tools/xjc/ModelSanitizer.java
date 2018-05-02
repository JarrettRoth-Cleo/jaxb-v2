package com.sun.tools.xjc;

import com.sun.tools.xjc.model.CClass;
import com.sun.tools.xjc.model.CClassInfo;
import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.nav.NClass;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelSanitizer {

    private List<String> nClassNames = new ArrayList<String>();
    private List<String> qnameNames = new ArrayList<String>();

    public Model sanitize(Model model){
        Map<NClass, Map<QName, CElementInfo>> x = model.getElementMappings();
        for(Map.Entry<NClass,Map<QName,CElementInfo>> currentElement : x.entrySet()){
            NClass key = currentElement.getKey();
            if(key!=null) {
                String packageName = key.fullName();
                for(Map.Entry<QName,CElementInfo> currentClassEntry : currentElement.getValue().entrySet()){
                    QName qnameKey = currentClassEntry.getKey();
                    System.out.println(key.fullName()+ " " +qnameKey.toString());
                }
            }
        }
        //TODO
        return model;
    }


}
