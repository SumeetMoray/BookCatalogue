package com.nearbyshops.communityLibrary.database.DaggerComponents;



import com.nearbyshops.communityLibrary.database.zzzDeprecatedCode.BooksActivityBackup;
import com.nearbyshops.communityLibrary.database.AllBooks.BooksActivity;
import com.nearbyshops.communityLibrary.database.BookDetails.BookDetail;
import com.nearbyshops.communityLibrary.database.BookMeetups.AddBookMeetup;
import com.nearbyshops.communityLibrary.database.BookMeetups.BookMeetupsFragment;
import com.nearbyshops.communityLibrary.database.BookMeetups.EditBookMeetup;
import com.nearbyshops.communityLibrary.database.BookReviews.BookReviews;
import com.nearbyshops.communityLibrary.database.BooksByCategory.BookCategories.BookCategoriesAdapter;
import com.nearbyshops.communityLibrary.database.BooksByCategory.BookCategories.BookCategoriesFragment;
import com.nearbyshops.communityLibrary.database.BooksByCategory.BookCategories.EditBookCategory;
import com.nearbyshops.communityLibrary.database.BooksByCategory.Books.AddBook;
import com.nearbyshops.communityLibrary.database.BooksByCategory.Books.BookAdapter;
import com.nearbyshops.communityLibrary.database.BooksByCategory.Books.BookFragment;
import com.nearbyshops.communityLibrary.database.BooksByCategory.Books.EditBook;
import com.nearbyshops.communityLibrary.database.DaggerModules.AppModule;
import com.nearbyshops.communityLibrary.database.DaggerModules.NetModule;
import com.nearbyshops.communityLibrary.database.BookDetails.RateReviewDialog;
import com.nearbyshops.communityLibrary.database.FavouriteBooks.FavouriteBooks;
import com.nearbyshops.communityLibrary.database.Login.EditProfile;
import com.nearbyshops.communityLibrary.database.Login.LoginDialog;
import com.nearbyshops.communityLibrary.database.Dialogs.SortFIlterBookDialog;
import com.nearbyshops.communityLibrary.database.Login.SignUp;
import com.nearbyshops.communityLibrary.database.SelectParent.BookCategoriesParent;
import com.nearbyshops.communityLibrary.database.SelectParent.BookCategoriesParentAdapter;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by sumeet on 14/5/16.
 */

@Singleton
@Component(modules={AppModule.class, NetModule.class})
public interface NetComponent {


    void Inject(BookCategoriesAdapter bookCategoriesAdapter);

    void Inject(BookCategoriesFragment bookCategoriesFragment);

    void Inject(BookCategoriesParent bookCategoriesParent);

    void Inject(BookCategoriesParentAdapter itemCategoriesParentAdapter);

    void Inject(EditBookCategory editBookCategory);

    void Inject(BookAdapter bookAdapter);

    void Inject(BookFragment bookFragment);

    void Inject(EditBook editBook);

    void Inject(AddBook addBook);

    void Inject(LoginDialog loginDialog);

    void Inject(SortFIlterBookDialog sortFIlterBookDialog);

    void Inject(RateReviewDialog rateReviewDialog);

    void Inject(BookDetail bookDetail);

    void Inject(BookReviews bookReviews);

    void Inject(SignUp signUp);

    void Inject(EditProfile editProfile);

    void Inject(FavouriteBooks favouriteBooks);

    void Inject(BooksActivity activityBooks);

    void Inject(BooksActivityBackup booksActivity);

    void Inject(BookMeetupsFragment bookMeetups);

    void Inject(EditBookMeetup editBookMeetup);

    void Inject(AddBookMeetup addBookMeetup);


//    void Inject(DetachedItemFragment detachedItemFragment);
}
