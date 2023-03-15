package io.xzxj.canal.core.client;

/**
 * @author xzxj
 * @date 2023/3/11 10:33
 */
public interface ICanalClient {

    /**
     * 初始化客户端
     */
    void init();


    /**
     * 销毁客户端
     */
    void destroy();


    /**
     * 执行监听
     */
    void handleListening();

}
