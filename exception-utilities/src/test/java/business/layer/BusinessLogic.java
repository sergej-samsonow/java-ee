package business.layer;

import framework.layer.FrameworkClassOne;

public class BusinessLogic {
    

    public FrameworkClassOne service;

    public void businessMethod() {
        service = new FrameworkClassOne();
        service.methodForBusinessLogic();
    }
}
