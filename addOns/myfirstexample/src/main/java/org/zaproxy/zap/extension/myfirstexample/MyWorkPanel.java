/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2020 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.myfirstexample;

import java.awt.CardLayout;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import org.parosproxy.paros.extension.AbstractPanel;
import org.zaproxy.zap.extension.tab.Tab;

/** @author daike */
@SuppressWarnings("serial")
public class MyWorkPanel extends AbstractPanel implements Tab {
    MyWorkPanel(String name, Logger LOGGER) {
        setLayout(new CardLayout());
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.setFont(new java.awt.Font("Dialog", java.awt.Font.PLAIN, 11));
        jScrollPane.setHorizontalScrollBarPolicy(
                javax.swing.JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        jScrollPane.setViewportView(new MyFirstJPanel());
        this.setName(
                name); // without calling this method, then NULL pointer exception will be occured.
        this.add(jScrollPane);
        if (LOGGER != null) {
            LOGGER.debug("new MyWorkPanel");
        }
    }
}
