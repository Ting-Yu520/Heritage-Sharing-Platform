// AllTests.java
package com.heritage.platform.controller;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        AuditLogControllerTest.class,
        CategoryControllerTest.class,
        CommentControllerTest.class,
        HelloControllerTest.class,
        NotificationControllerTest.class,
        ResourceControllerTest.class,
        UserControllerTest.class
})
public class AllTests {
}