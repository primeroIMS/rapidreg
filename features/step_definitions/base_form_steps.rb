When /^I fill in the following:$/ do |table|
  table.rows_hash.each do |field, value|
    until base_page.ifTextExist(field) do
      case_page.scrollToNextFields
    end
    case_page.scrollLittleUp
    puts field
    base_form.fillInForm(field, value)
    puts value
  end
end

Then /^I should see following:$/ do |table|
  table.rows_hash.each do |field, value|
    until base_page.ifTextExist(field) do
      case_page.scrollToNextFields
    end
    case_page.scrollLittleUp
    puts field
    base_form.verifyFormValue(field,value)
    puts value
  end
end