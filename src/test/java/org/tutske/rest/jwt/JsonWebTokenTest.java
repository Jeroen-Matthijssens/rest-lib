package org.tutske.rest.jwt;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;
import static org.tutske.rest.jwt.JsonWebToken.Keys.*;

import com.google.gson.GsonBuilder;
import org.junit.Test;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.Map;


public class JsonWebTokenTest {

	private JsonWebToken token = JsonWebToken.fromString ("hh.bb.ss");

	@Test
	public void it_should_be_in_base64 () {
		assertThat (token.toString (), not (containsString ("{")));
	}

	@Test
	public void it_should_keep_the_old_values_when_creating_a_token_with_different_values () {
		JsonWebToken changed = token.with (Authentication, new byte [] { (byte) 0xa2, (byte) 0x47 });
		assertThat (changed.get (Payload), is (token.get (Payload)));
	}

	@Test
	public void it_should_have_the_new_value_when_creating_a_token_with_different_values () {
		JsonWebToken changed = token.with (Payload, new byte [] { (byte) 0xa2, (byte) 0x47 });
		assertThat (changed.get (Payload), is (new byte [] { (byte) 0xa2, (byte) 0x47 }));
	}

	@Test
	public void it_should_create_a_token_from_string_without_signature () {
		JsonWebToken token = JsonWebToken.fromString ("aaa.aaa");
		assertThat (token.get (Authentication), is (new byte [] {}));
		assertThat (token.get (Payload), not (nullValue ()));
	}

	@Test
	public void it_should_create_a_token_from_string_with_signature () {
		JsonWebToken token = JsonWebToken.fromString ("aaa.aaa.aaa");
		assertThat (token.get (Authentication), not (nullValue ()));
		assertThat (token.get (Payload), not (nullValue ()));
	}

	@Test
	public void it_should_get_the_payload_from_the_token () {
		String body = "{\"principal\": \"jhon.doe@example.com\"}";
		JsonWebToken token = JsonWebToken.fromString ("." + Base64.getEncoder ().encodeToString (body.getBytes ()) + ".");

		String retrieved = token.getPayload ();

		assertThat (retrieved, is (body));
	}

	@Test
	public void it_should_allow_converting_the_payload_into_some_object () {
		String body = "{\"principal\": \"jhon.doe@example.com\"}";
		JsonWebToken token = JsonWebToken.fromString ("." + Base64.getEncoder ().encodeToString (body.getBytes ()) + ".");

		Map<?, ?> retrieved = token.getPayload ((content) -> {
			return new GsonBuilder ().create ().fromJson (content, Map.class);
		});

		assertThat (retrieved, hasKey ("principal"));
		assertThat (retrieved.get ("principal"), is ("jhon.doe@example.com"));
	}

	@Test
	public void it_should_make_a_web_token_from_any_object () {
		Map<String, String> values = new LinkedHashMap<> ();
		values.put ("sub", "jhon.doe@example.com");

		JsonWebToken token = JsonWebToken.fromData (values);

		assertThat (token.getPayload (), containsString ("jhon.doe@example.com"));
	}

	@Test
	public void it_should_turn_a_token_back_into_the_data () {
		Map<String, String> values = new LinkedHashMap<> ();
		values.put ("sub", "jhon.doe@example.com");

		JsonWebToken token = JsonWebToken.fromData (values);
		Map<?, ?> retrieved = token.getPayload (Map.class);

		assertThat (retrieved, hasKey ("sub"));
		assertThat (retrieved.get ("sub"), is ("jhon.doe@example.com"));
	}

	@Test (expected = IllegalArgumentException.class)
	public void it_should_complain_when_creating_a_token_with_incorrect_parts () {
		new JsonWebToken (new byte [2288][]);
	}

	@Test (expected = IllegalArgumentException.class)
	public void it_should_complain_when_creating_from_a_string_with_incorrect_parts () {
		JsonWebToken.fromString ("..........................................");
	}

}
