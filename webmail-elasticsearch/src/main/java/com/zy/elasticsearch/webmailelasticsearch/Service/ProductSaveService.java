package com.zy.elasticsearch.webmailelasticsearch.Service;


import com.zy.common.to.es.SkuEsModel;

import java.io.IOException;
import java.util.List;

public interface ProductSaveService {

    boolean productStatusUp(List<SkuEsModel> esModels) throws IOException;
}
