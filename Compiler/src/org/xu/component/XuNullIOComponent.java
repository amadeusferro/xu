package org.xu.component;

public class XuNullIOComponent extends XuIOComponent<XuNullIOComponent> {

    private static final XuNullIOComponent instance = new XuNullIOComponent();

    public XuNullIOComponent() {

    }

    @Override
    public XuIOComponent<XuNullIOComponent> clone() {
        return instance;
    }
}
