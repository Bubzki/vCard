package bubzki.vcard;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.NoSuchElementException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

public class ContactCardTests {
	private static final String[] GOOD = {
			"FN:Forrest Gump\r\nORG:Bubba Gump Shrimp Co.\r\nGENDER:M\r\nTEL;TYPE=HOME:4951234567", 
			"FN:Chuck Norris\r\nORG:Hollywood\r\nBDAY:10-04-1940\r\nTEL;TYPE=WORK:1234567890", 
	};
	private static final String[] BAD = {
			"FN:Forrest Gump\r\nORG:Bubba Gump Shrimp Co.\r\nGENDER:M\r\nBDAY:06-06-1944\r\nTEL;TYPE=HOME:+1 234-567", //wrong phone format
			"FN:Chuck Norris\r\nORG:Hollywood\r\nBDAY:10-04-1940\r\nTEL;TYPE=WORK:12345678901", //digits > 10
	};
	
	private final ContactCard card = new ContactCard();

	@Timeout(1)

	private ContactCard getCard(String text){
		return card.getInstance("BEGIN:VCARD\r\n"+text+"\r\nEND:VCARD");
	}

	@Test
	public void getFullName1(){
		String text = GOOD[1];
		ContactCard card = getCard(text);
		assertEquals("Chuck Norris", card.getFullName(),
				"getFullName() failed with this text:\n"+text+"\n");
	}
	
	@Test
	public void getOrganization0(){
		String text = GOOD[0];
		ContactCard card = getCard(text);
		assertEquals("Bubba Gump Shrimp Co.", card.getOrganization(),
				"getOrganization() failed with this text:\n"+text+"\n");
	}
	
	@Test
	public void getBirthdayException(){
		assertThrows(NoSuchElementException.class, () -> getCard(GOOD[0]).getBirthday());
	}

	@Test
	public void getAgeYears(){
		Calendar today = Calendar.getInstance(); 
		Calendar bday = Calendar.getInstance();
		bday.set(1940, Calendar.APRIL, 10);
		int age = today.get(Calendar.YEAR) - bday.get(Calendar.YEAR); 
		bday.add(Calendar.YEAR, age); 
		if (today.before(bday)) {
			age--;
		}
		String text = GOOD[1];
		ContactCard card = getCard(text);
		assertEquals(age, card.getAgeYears(), "getAgeYears() failed with this text:\n"+text+"\n");
	}
	
	@Test
	public void getPhone0(){
		String text = GOOD[0];
		ContactCard card = getCard(text);
		assertEquals("(495) 123-4567", card.getPhone("HOME"),
				"getPhone(\"HOME\") failed with this text:\n"+text+"\n");
	}
	
	@Test
	public void getPhone1(){
		String text = GOOD[1];
		ContactCard card = getCard(text);
		assertEquals("(123) 456-7890", card.getPhone("WORK"),
				"getPhone(\"WORK\") failed with this text:\n"+text+"\n");
	}
	
	@Test
	public void phoneExceptionNotNumber(){
		assertThrows(InputMismatchException.class, () -> getCard(BAD[0]));
	}
	
	@Test
	public void phoneExceptionNot10Digits(){
		assertThrows(InputMismatchException.class, () ->getCard(BAD[1]));
	}
	
}