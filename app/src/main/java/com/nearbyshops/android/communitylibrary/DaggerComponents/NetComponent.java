package com.nearbyshops.android.communitylibrary.DaggerComponents;



import com.nearbyshops.android.communitylibrary.BookDetails.BookDetail;
import com.nearbyshops.android.communitylibrary.BookReviews.BookReviews;
import com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategories.BookCategoriesAdapter;
import com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategories.BookCategoriesFragment;
import com.nearbyshops.android.communitylibrary.BooksByCategory.BookCategories.EditBookCategory;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Books.AddBook;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Books.BookAdapter;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Books.BookFragment;
import com.nearbyshops.android.communitylibrary.BooksByCategory.Books.EditBook;
import com.nearbyshops.android.communitylibrary.DaggerModules.AppModule;
import com.nearbyshops.android.communitylibrary.DaggerModules.NetModule;
import com.nearbyshops.android.communitylibrary.BookDetails.RateReviewDialog;
import com.nearbyshops.android.communitylibrary.Login.EditProfile;
import com.nearbyshops.android.communitylibrary.Login.LoginDialog;
import com.nearbyshops.android.communitylibrary.Dialogs.SortFIlterBookDialog;
import com.nearbyshops.android.communitylibrary.Login.SignUp;
import com.nearbyshops.android.communitylibrary.SelectParent.BookCategoriesParent;
import com.nearbyshops.android.communitylibrary.SelectParent.BookCategoriesParentAdapter;

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


//    void Inject(DetachedItemFragment detachedItemFragment);
}
