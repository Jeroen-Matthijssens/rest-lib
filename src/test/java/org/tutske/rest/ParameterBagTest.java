package org.tutske.rest;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.*;

import org.junit.Test;
import org.tutske.rest.exceptions.ResponseException;

import java.util.HashMap;


public class ParameterBagTest {

	@Test
	public void it_should_remember_the_values () {
		ParameterBag<String> bag = new ParameterBag<> ();
		bag.add ("key", "value");
		assertThat (bag, hasEntry ("key", "value"));
	}

	@Test
	public void it_should_remember_multiple_values_on_the_same_key () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "first value");
			add ("key", "second value");
		}};

		assertThat (bag, hasKey ("key"));
		assertThat (bag, hasEntry ("key", "first value"));
		assertThat (bag.getAll ("key"), containsInAnyOrder ("first value", "second value"));
	}

	@Test
	public void it_should_should_allow_adding_mulitple_values_to_the_same_key_at_once () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "first value", "second value");
		}};

		assertThat (bag, hasKey ("key"));
		assertThat (bag, hasEntry ("key", "first value"));
		assertThat (bag.getAll ("key"), containsInAnyOrder ("first value", "second value"));
	}

	@Test
	public void it_should_not_change_the_primary_value_when_adding_on_a_key_with_a_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value");
		}};

		bag.add ("key", "secondary value");

		assertThat (bag.get ("key"), is ("primary value"));
	}

	@Test
	public void it_should_change_the_primary_value_when_putting_on_a_key_with_a_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value");
		}};

		bag.put ("key", "new primary value");

		assertThat (bag.get ("key"), is ("new primary value"));
	}

	@Test
	public void it_should_retain_the_old_primary_value_when_putting_on_a_key_with_a_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value");
		}};

		bag.put ("key", "new primary value");

		assertThat (bag.getAll ("key"), hasItem ("old primary value"));
	}

	@Test
	public void it_should_have_the_new_primary_value_when_replacing_on_a_key () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value");
		}};

		bag.replace ("key", "new primary value");

		assertThat (bag.get ("key"), is ("new primary value"));
	}

	@Test
	public void it_should_no_longer_have_the_old_primary_value_when_replacing_on_a_key () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value");
		}};

		bag.replace ("key", "new primary value");

		assertThat (bag.getAll ("key"), not (hasItem ("second value")));
	}

	@Test
	public void it_should_still_have_all_the_secondary_items_when_replacing_on_a_key () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value", "secondary value");
		}};

		bag.replace ("key", "new primary value");

		assertThat (bag.getAll ("key"), hasItem ("secondary value"));
	}

	@Test
	public void it_should_replace_the_primary_value_when_replacing_on_a_key_with_an_old_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value");
		}};

		bag.replace ("key", "wrong old primary value", "new primary value");

		assertThat (bag.get ("key"), is ("old primary value"));
	}

	@Test
	public void it_should_not_replace_the_primary_value_when_replacing_on_a_key_with_an_old_secondary_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value", "old secondary value");
		}};

		bag.replace ("key", "old secondary value", "new secondary value");

		assertThat (bag.get ("key"), is ("old primary value"));
	}

	@Test
	public void it_should_replace_secondary_values_when_replacing_on_a_key_with_an_old_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value", "old secondary value");
		}};

		bag.replace ("key", "old secondary value", "new secondary value");

		assertThat (bag.getAll ("key"), hasItem ("new secondary value"));
		assertThat (bag.getAll ("key"), not (hasItem ("old secondary value")));
	}

	@Test
	public void it_should_only_replace_when_the_old_values_match () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value", "old secondary value");
		}};

		bag.replace ("key", "wrong old value", "new value");

		assertThat (bag.getAll ("key"), not (hasItem ("new value")));
		assertThat (bag.getAll ("key"), hasItem ("old primary value"));
		assertThat (bag.getAll ("key"), hasItem ("old secondary value"));
	}

	@Test
	public void it_should_remove_the_primary_value_when_removing_on_a_key () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value", "secondary value");
		}};

		bag.remove ("key");

		assertThat (bag.getAll ("key"), not (hasItem ("primary value")));
		assertThat (bag.get ("key"), is ("secondary value"));
	}

	@Test
	public void it_should_return_null_when_removing_a_key_that_is_not_in_the_bag () {
		ParameterBag<String> bag = new ParameterBag<> ();
		assertThat (bag.remove ("key"), nullValue ());
	}

	@Test
	public void it_should_remove_a_primary_value_when_removing_on_a_key_and_old_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value", "secondary value");
		}};

		bag.remove ("key", "primary value");

		assertThat (bag.getAll ("key"), not (hasItem ("primary value")));
	}

	@Test
	public void it_should_remove_a_secondary_value_when_removing_on_a_key_and_old_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value", "secondary value");
		}};

		bag.remove ("key", "secondary value");

		assertThat (bag.getAll ("key"), not (hasItem ("secondary value")));
	}

	@Test
	public void it_should_have_a_secondary_value_when_removing_the_primary_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value", "secondary value");
		}};

		bag.remove ("key", "primary value");

		assertThat (bag.get ("key"), is ("secondary value"));
	}

	@Test
	public void it_should_clear_all_the_values_of_single_key () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("odds", "first", "third");
			add ("evens", "second", "forth");
		}};

		bag.clear ("odds");

		assertThat (bag, not (hasKey ("odds")));
	}

	@Test
	public void it_should_put_values_from_a_map () {
		ParameterBag<String> bag = new ParameterBag<> ();
		bag.putAll (new HashMap<String, String> () {{
			put ("key", "value");
		}});

		assertThat (bag, hasEntry ("key", "value"));
	}

	@Test
	public void it_should_put_values_when_adding_a_map_as_the_primary_values () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value");
		}};
		bag.putAll (new HashMap<String, String> () {{
			put ("key", "new primary value");
		}});

		assertThat (bag, hasEntry ("key", "new primary value"));
	}

	@Test
	public void it_should_keep_the_original_values_as_secondary_when_putting_from_a_map () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "old primary value");
		}};
		bag.putAll (new HashMap<String, String> () {{
			put ("key", "new primary value");
		}});

		assertThat (bag.getAll ("key"), hasItem ("new primary value"));
	}

	@Test
	public void it_should_add_the_values_in_the_map_as_secondary_when_adding_them () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value");
		}};
		bag.addAll (new HashMap<String, String> () {{
			put ("key", "secondary value");
		}});

		assertThat (bag.getAll ("key"), hasItem ("secondary value"));
	}

	@Test
	public void it_should_not_modify_the_primary_values_when_adding_values_from_a_map () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value");
		}};
		bag.addAll (new HashMap<String, String> () {{
			put ("key", "secondary value");
		}});

		assertThat (bag, hasEntry ("key", "primary value"));
	}

	@Test
	public void it_should_add_all_the_values_of_an_other_bag_as_secondary_values () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value", "secondary value");
		}};

		bag.addAll (new ParameterBag<String> (){{
			add ("key", "extra primary", "extra secondary");
		}});

		assertThat (bag.get ("key"), is ("primary value"));
		assertThat (bag.getAll ("key"), containsInAnyOrder (
			"primary value", "secondary value", "extra primary", "extra secondary"
		));
	}

	@Test
	public void it_should_no_longer_contain_a_key_if_all_values_are_removed () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "value");
		}};
		bag.remove ("key");
		assertThat (bag.containsKey ("key"), is (false));
		assertThat (bag, not (hasKey ("key")));
	}

	@Test
	public void it_should_no_longer_contain_a_key_if_all_values_are_removed_with_old_values () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "value");
		}};
		bag.remove ("key", "value");
		assertThat (bag.containsKey ("key"), is (false));
		assertThat (bag, not (hasKey ("key")));
	}

	@Test
	public void it_should_no_longer_contain_a_key_if_all_values_are_cleared () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "value");
		}};
		bag.clear ("key");
		assertThat (bag.containsKey ("key"), is (false));
		assertThat (bag, not (hasKey ("key")));
	}

	@Test
	public void it_should_say_it_contains_primary_values () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value");
		}};
		assertThat (bag.containsValue ("primary value"), is (true));
	}

	@Test
	public void it_should_say_it_contains_secondary_values () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "primary value", "secondary value");
		}};
		assertThat (bag.containsValue ("secondary value"), is (true));
	}

	@Test
	public void it_should_parse_string_values_to_integers () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "1");
		}};

		assertThat (bag.converted ("key", Integer.class), is (1));
	}

	@Test
	public void it_should_parse_string_values_to_longs () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "1");
		}};

		assertThat (bag.converted ("key", Long.class), is (1L));
	}

	@Test
	public void it_should_parse_string_values_to_floats () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "1");
		}};

		assertThat (bag.converted ("key", Float.class), is (1F));
	}

	@Test
	public void it_should_parse_string_values_to_floats_with_decimal_part () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "1.2");
		}};

		assertThat (bag.converted ("key", Float.class), is (1.2F));
	}

	@Test
	public void it_should_parse_string_values_to_booleans () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "false");
		}};

		assertThat (bag.converted ("key", Boolean.class), is (false));
	}

	@Test (expected = ResponseException.class)
	public void it_should_give_a_response_exception_when_parsing_of_values_fails () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "one");
		}};

		assertThat (bag.converted ("key", Integer.class), is (false));
	}

	@Test (expected = RuntimeException.class)
	public void it_should_complain_when_asking_to_convert_to_an_unknown_class () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key", "one");
		}};

		assertThat (bag.converted ("key", ParameterBagTest.class), is (false));
	}

	@Test
	public void it_should_know_it_has_keys_even_when_there_is_no_value () {
		ParameterBag<String> bag = new ParameterBag<String> () {{
			add ("key");
		}};

		assertThat (bag.containsKey ("key"), is (true));
		assertThat (bag.get ("key"), nullValue ());
	}

}
