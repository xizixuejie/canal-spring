package io.xzxj.canal.spring.client;

import io.xzxj.canal.core.client.ICanalClient;

import java.util.ArrayList;

public class CanalClientList extends ArrayList<ICanalClient> {

    public void init() {
        this.forEach(ICanalClient::init);
    }

    public void destroy() {
        this.forEach(ICanalClient::destroy);
    }

}