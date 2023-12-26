package io.xzxj.canal.core.handler;

/**
 * @author xzxj
 * @date 2023/3/11 10:44
 */
public interface IMessageHandler<T> {

    void handleMessage(String destination, T t) throws Exception;

}
