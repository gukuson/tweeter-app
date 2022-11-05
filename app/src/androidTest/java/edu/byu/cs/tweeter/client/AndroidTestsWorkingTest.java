package edu.byu.cs.tweeter.client;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import edu.byu.cs.tweeter.client.cache.Cache;
import edu.byu.cs.tweeter.client.model.service.StatusService;
import edu.byu.cs.tweeter.client.presenter.MainPresenter;
import edu.byu.cs.tweeter.model.domain.Status;

/**
 * This class exists purely to prove that tests in your androidTest/java folder have the correct dependencies.
 * Click on the green arrow to the left of the class declarations to run. These tests should pass if all
 * dependencies are correctly set up.
 */
public class AndroidTestsWorkingTest {
    class Foo {
        public void foo() {

        }
    }


    @Test
    public void testAsserts() {
        Assertions.assertTrue(true);
    }
    @Test
    public void testMockitoSpy() {
        Foo f = Mockito.spy(new Foo());
        f.foo();
        Mockito.verify(f).foo();
    }
    @Test
    public void testMockitoMock() {
        Foo f = Mockito.mock(Foo.class);
        f.foo();
        Mockito.verify(f).foo();
    }
}
