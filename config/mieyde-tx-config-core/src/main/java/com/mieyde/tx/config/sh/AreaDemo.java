package com.mieyde.tx.config.sh;

import cn.hutool.extra.pinyin.PinyinUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mieyde.tx.common.util.CollectionUtils;
import com.mieyde.tx.common.util.FileUtils;
import com.mieyde.tx.common.util.ObjectUtils;
import com.mieyde.tx.common.util.StringUtls;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 我吃稀饭面
 * @date 2023/8/8 13:11
 */
public class AreaDemo {

    public static Area root;
    public static List<String> datas;

    public static void main(String[] args) {
        List<Area> areaList = new ArrayList<>();
        initDatas();
        Map<String, List<String>> areaDatas = datas.stream().collect(Collectors.groupingBy(info -> info.split(":")[0]));
        for (Map.Entry<String, List<String>> entry : areaDatas.entrySet()) {
            String key = entry.getKey();
            List<String> value = entry.getValue();
            String areaName = key.split("-")[2];
            String streetName = key.split("-")[3];
            Area area = find(areaName, streetName, null);
            if (ObjectUtils.isNull(area)){
                System.out.println("区域街道不存在:" + key);
                continue;
            }
            int num = 1;
            if (CollectionUtils.isNotEmpty(area.getChildArea())){
                num = area.getChildArea().stream().map(demo -> Integer.valueOf(demo.getCode().substring(9, 12))).max(Comparator.comparing(s -> s)).orElse(0);
                num++;
            }
            for (String data : value) {
                Area areaData = find(data);
                if (ObjectUtils.isNull(areaData)){
                    //新建社区
                    Area newArea = buildArea(area, data, num);
                    areaList.add(newArea);
                    num++;
                }else {
                    System.out.println("此社区已存在：" + data);
                }
            }
        }

        System.out.println(JSONArray.toJSONString(areaList));
        System.out.println(areaList.size());
    }

    private static Area buildArea(Area parentArea,String data,int num){
        Area area = new Area();
        area.setName(data.split(":")[1]);
        area.setPCode(parentArea.getCode());
        area.setLevel("COMMUNITY");
        area.setCode(parentArea.getCode() + String.format("%03d",num));
        area.setPinyin(PinyinUtil.getFirstLetter(area.getName(),""));
        area.setDetail(parentArea.getDetail() + area.getName());
        area.set_class(parentArea._class);
        return area;
    }

    private static Area find(String data){
        String[] split = data.split("[-,:]");
        String area = split[2];
        String street = split[3];
        String community = split[4];
        return find(area, street, community);
    }

    private static Area find(String area,String street,String community){
        if (StringUtls.isBlank(area)){
            throw new RuntimeException("地区不能为空");
        }
        int flag = 1;
        if (StringUtls.isNotBlank(street) && StringUtls.isBlank(community)){
            flag = 2;
        }else if (StringUtls.isNotBlank(street) && StringUtls.isNotBlank(community)){
            flag = 3;
        }else if (StringUtls.isBlank(street) && StringUtls.isNotBlank(community)){
            throw new RuntimeException("请按照参数进行传递");
        }

        if (flag == 1){
            return findChild(root.getChildArea(),area);
        }else if (flag == 2){
            Area child = findChild(root.getChildArea(), area);
            if (ObjectUtils.isNull(child)){
                return null;
            }
            return findChild(child.getChildArea(),street);
        }else if (flag == 3){
            Area child = findChild(root.getChildArea(), area);
            if (ObjectUtils.isNull(child)){
                return null;
            }
            Area child1 = findChild(child.getChildArea(), street);
            if (ObjectUtils.isNull(child1)){
                return null;
            }
            return findChild(child1.getChildArea(),community);
        }
        return null;
    }

    private static Area findChild(List<Area> childArea,String name){
        if(CollectionUtils.isEmpty(childArea)){
            return null;
        }
        for (Area demo : childArea) {
            if (demo.getName().equals(name)) {
                return demo;
            }
        }
        return null;
    }

    private static void initDatas(){
        String content = FileUtils.getContent(FileUtils.load("area.json"));
        List<Area> fullDatas = parseContent(content);
        Area rootArea = fullDatas.stream().filter(demo -> demo.getLevel().equals("CITY")).collect(Collectors.toList()).get(0);
        findChild(rootArea,fullDatas);
        root = rootArea;

        String content1 = FileUtils.getContent(FileUtils.load("t3.file"));
        String[] split = content1.split("\n");
        datas = Arrays.asList(split);
    }

    private static List<Area> findChild(Area area,List<Area> fullDatas){
        List<Area> childAreas = fullDatas.stream().filter(demo -> demo.getPCode().equals(area.getCode())).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(childAreas)){
            return null;
        }
        for (Area childArea : childAreas) {
            List<Area> child = findChild(childArea, fullDatas);
            childArea.setParent(area);
            if (CollectionUtils.isNotEmpty(child)){
                childArea.setChildArea(child);
            }
        }
        area.setChildArea(childAreas);
        return childAreas;
    }

    private static List<Area> parseContent(String content){
        JSONArray jsonArray = JSONArray.parseArray(content);
        List<Area> datas = new ArrayList<>();
        for (Object o : jsonArray) {
            JSONObject object = (JSONObject) o;
            Area area = new Area();
            area.setCode(object.getString("code"));
            area.setPinyin(object.getString("pinyin"));
            area.setLevel(object.getString("level"));
            area.setPCode(object.getString("pCode"));
            area.setName(object.getString("name"));
            area.set_class(object.getString("_class"));
            area.setDetail(object.getString("detail"));
            datas.add(area);
        }
        return datas;
    }

    @Getter
    @Setter
    private static class Area{
        private String name;
        private String pCode;
        private String level;
        private String code;
        private String pinyin;
        private String detail;
        private String _class;
        private Area parent;
        private List<Area> childArea;
    }
}
