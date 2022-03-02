package com.example.libraryapplication.viewmodels.response

class Report {
    enum class LoginResponse {
        SUCCESS, INVALID_MOBILE_NO, INVALID_PASSWORD
    }

    enum class SignupResponse(val reason: String) {
        SUCCESS("Signed up Successfully"), INVALID_MOBILE_NO("Mobile No Invalid"), MOBILE_NO_ALREADY_TAKEN(
            "This no is already taken"
        ),
        PASSWORD_CONFIRMATION_FAILED("Password Mismatch"), INVALID_PIN_CODE("Please enter a valid pinCode"), INVALID_DOOR_NO(
            "No Special Characters Allowed"
        ),
        INVALID_PASSWORD(
            "password must contain atLeast 8 character with :\n1 Special char(!@#\$%^&*)," +
                    "1 Capital letter,1 number,1 small letter"
        ),
        NAME_EMPTY("Name should be filled"), MOBILE_EMPTY("Mobile should be filled"), PIN_CODE_EMPTY(
            "Pin Code should be filled"
        ),
        DOOR_NO_EMPTY("Door No should be filled"), DISTRICT_EMPTY("District should be filled"),
        PASSWORD_EMPTY("Password Should be Filled"), UN_FILLED_ROWS("Unfilled Rows"), LAND_MARK_EMPTY(
            "LandMark should be mentioned"
        ),
        STREET_NAME_EMPTY("Street name or village name Should be filled")
    }

    enum class BookCheckResponse(val reason: String) {
        SUCCESS("Register Successful"), UN_FILLED_ROWS("Unfilled rows"), SELECT_IMAGE("Image should be selected"),
        BOOK_NAME_EMPTY("Book Name should be filled"), AUTHOR_EMPTY("Author Name should be filled"), SUBJECT_EMPTY(
            "Subject should be filled"
        ),
        NO_OF_PAGES_EMPTY("No of Pages should be filled"), INVALID_PAGE_NO("Invalid No of pages")
    }
}