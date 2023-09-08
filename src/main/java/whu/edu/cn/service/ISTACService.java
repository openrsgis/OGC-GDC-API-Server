package whu.edu.cn.service;

import whu.edu.cn.entity.stac.*;

public interface ISTACService {
    Collections getSTACollections();

    Collection getSTACCollection(String collectionName);

    Queryables getSTACQueryables(String collectionName);

    STACItems getSTACItems(String collectionName, String bbox, String filter, String datetime, int pageNum, int pageSize);

    STACItem getSTACItem(String collectionName, String itemName);


}
