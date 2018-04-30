package com.sun.tools.xjc;

import com.sun.tools.xjc.model.CElementInfo;
import com.sun.tools.xjc.model.Model;
import com.sun.tools.xjc.model.nav.NClass;

import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ModelSanitizer {

    public Model sanitize(Model model){
        Map<NClass, Map<QName, CElementInfo>> x = model.getElementMappings();
        List<Map<QName, CElementInfo>> maps = new ArrayList<Map<QName, CElementInfo>>(x.values());
        List<String> names = getNamesOfMappedElements(maps);
        //Adding a comment for testing the git branch issue

        for(String collectedName : names){
            for (String test : names){
                if (collectedName.equalsIgnoreCase(test)) {
                    System.out.println("found dupe" + test + "too similar to " + collectedName);
                }
            }

        }
        //TODO
        return model;
    }
    private static List<String> getNamesOfMappedElements(List<Map<QName, CElementInfo>> rootMaps){
        List<String> classNameList = new ArrayList<String>();
        for(Map<QName, CElementInfo> m : rootMaps) {
            getNameAndSubElementNames(classNameList, null, m);
        }
        return classNameList;
    }

    private static void getNameAndSubElementNames(List<String> accumulatedNames, String elementName, Map<QName, CElementInfo> mapOfChildren){
        if (elementName != null){
            accumulatedNames.add(elementName);
        }

        /*
        for (int r = 0 ; r < mapOfChildren.size() ; r++){
        }
        */
        for (Map.Entry<QName, CElementInfo> e: mapOfChildren.entrySet()){
            QName key = e.getKey();
            String simpleName = key.getLocalPart();
            if (simpleName != null){
                accumulatedNames.add(simpleName);
            }

            //Map<QName, CElementInfo> map = new HashMap<QName, CElementInfo>();
            //map.put(e.getKey(), e.getValue());
            //getNameAndSubElementNames(accumulatedNames, x, map);
        }
    }
}
