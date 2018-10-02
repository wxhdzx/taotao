package com.taotao.manage.bean;

import org.apache.http.conn.HttpClientConnectionManager;

/**
 * 定期清理无效的http连接
 */
public class IdleConnectionEvictor extends Thread {

    private final HttpClientConnectionManager connMgr;
    
  
    private volatile boolean shutdown;

    public IdleConnectionEvictor(HttpClientConnectionManager connMgr) {
        this.connMgr = connMgr;
        
        this.start();
    }

    @Override
    public void run() {
        try {
            while (!shutdown) {
                synchronized (this) {
                    wait(5000 );
                    // 关闭失效的连接
                    connMgr.closeExpiredConnections();
                }
            }
        } catch (InterruptedException ex) {
            // 结束
        }
    }

    /**
     * 销毁释放资源
     */
    public void shutdown() {
        shutdown = true;
        synchronized (this) {
            notifyAll();
        }
    }
}
