import org.apache.commons.lang.RandomStringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Alex
 * Date: 29/5/2013
 * Time: 1:28 μμ
 * To change this template use File | Settings | File Templates.
 */
public class Test {

	public Test() {
		System.out.println(RandomStringUtils.randomAlphanumeric(15).toUpperCase());
	}

	public static void main(String args[]) {
		new Test();
	}
}
