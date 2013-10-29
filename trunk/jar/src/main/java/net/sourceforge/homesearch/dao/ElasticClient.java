package net.sourceforge.homesearch.dao;

//import org.elasticsearch.client.Client;
//import org.elasticsearch.node.Node;
//import org.elasticsearch.node.NodeBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Properties;

/**
 * Author: Vitaly Sazanovich
 * Email: vitaly.sazanovich@gmail.com
 * Date: 08/10/13
 * Time: 18:50
 */
public class ElasticClient {
//    private Client client;
    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    @Autowired
    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void init() throws Exception {
//        Node node = NodeBuilder.nodeBuilder().clusterName(properties.getProperty("elastic.clusterName")).local(true).node();
//        client = node.client();
    }

    public void destroy() throws Exception {
//        client.close();
    }
}
