/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package spring.reactor.promise;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.Environment;
import reactor.core.composable.Deferred;
import reactor.core.composable.Promise;
import reactor.core.composable.spec.Promises;

/**
 *
 * @author kent
 */
@Component
//@EnableReactor
public class PromiseBeanFactory implements FactoryBean<Deferred<String, Promise<String>>> {

    @Autowired
    Environment env;

    @Override
    public Deferred<String, Promise<String>> getObject() throws Exception {
        return Promises.<String>defer(env);
    }

    @Override
    public Class<?> getObjectType() {
        return Deferred.class;
    }

    @Override
    public boolean isSingleton() {
        return false;
    }

}
