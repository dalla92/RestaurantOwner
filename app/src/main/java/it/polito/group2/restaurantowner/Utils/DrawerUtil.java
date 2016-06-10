package it.polito.group2.restaurantowner.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.MenuItem;

import it.polito.group2.restaurantowner.R;
import it.polito.group2.restaurantowner.firebasedata.Restaurant;
import it.polito.group2.restaurantowner.gallery.GalleryViewActivity;
import it.polito.group2.restaurantowner.login.LoginManagerActivity;
import it.polito.group2.restaurantowner.owner.AddRestaurantActivity;
import it.polito.group2.restaurantowner.owner.MainActivity;
import it.polito.group2.restaurantowner.owner.MenuRestaurant_page;
import it.polito.group2.restaurantowner.owner.my_offers.MyOffersActivity;
import it.polito.group2.restaurantowner.owner.reservations.ReservationActivity;
import it.polito.group2.restaurantowner.owner.reviews.ReviewsActivity;
import it.polito.group2.restaurantowner.owner.statistics.StatisticsActivity;
import it.polito.group2.restaurantowner.user.my_orders.MyOrdersActivity;
import it.polito.group2.restaurantowner.user.my_reviews.MyReviewsActivity;
import it.polito.group2.restaurantowner.user.restaurant_list.UserRestaurantList;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyFavourites;
import it.polito.group2.restaurantowner.user.restaurant_page.UserMyReservations;
import it.polito.group2.restaurantowner.user.restaurant_page.UserProfile;
import it.polito.group2.restaurantowner.user.restaurant_page.UserRestaurantActivity;

/**
 * Created by Alessio on 10/06/2016.
 */
public class DrawerUtil {

    public static boolean drawer_owner_main_activity(Activity a, MenuItem item) {
        int id = item.getItemId();

        if(id== R.id.action_user_part) {
            Intent intent1 = new Intent(
                    a,
                    UserRestaurantList.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            a.startActivity(intent1);
            return true;
        }

        if(id==R.id.nav_logout) {
            Intent intent = new Intent(a, LoginManagerActivity.class);
            intent.putExtra("login", false);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            a.startActivity(intent);
            return true;
        }

        DrawerLayout drawer = (DrawerLayout) a.findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean drawer_owner_restaurant_page(Activity a, MenuItem item, String restaurant_id, Restaurant my_restaurant) {
        int MODIFY_INFO = 4;
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) a.findViewById(R.id.drawer_layout);
        if(id==R.id.action_my_restaurants){
            if (a instanceof MainActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent1 = new Intent(
                        a.getApplicationContext(),
                        MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                a.startActivity(intent1);
            }
            return true;

        } else if(id==R.id.action_show_as) {
            Intent intent1 = new Intent(
                    a.getApplicationContext(),
                    UserRestaurantActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurant_id);
            intent1.putExtras(b);
            a.startActivity(intent1);
            return true;
        } else if(id==R.id.action_gallery) {
            if (a instanceof GalleryViewActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent1 = new Intent(
                        a.getApplicationContext(),
                        GalleryViewActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b = new Bundle();
                b.putString("restaurant_id", restaurant_id);
                intent1.putExtras(b);
                a.startActivity(intent1);
            }
            return true;
        } else if(id==R.id.action_menu) {
            if (a instanceof MenuRestaurant_page ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent1 = new Intent(
                        a.getApplicationContext(),
                        MenuRestaurant_page.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b = new Bundle();
                b.putString("restaurant_id", restaurant_id);
                intent1.putExtras(b);
                a.startActivity(intent1);
            }
            return true;
        } else if(id==R.id.action_offers) {
            if (a instanceof MyOffersActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent2 = new Intent(
                        a.getApplicationContext(),
                        MyOffersActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b2 = new Bundle();
                b2.putString("restaurant_id", restaurant_id);
                intent2.putExtras(b2);
                a.startActivity(intent2);
            }
            return true;
        } else if(id==R.id.action_reservations){
            if (a instanceof ReservationActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent3 = new Intent(
                        a.getApplicationContext(),
                        ReservationActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b3 = new Bundle();
                b3.putString("restaurant_id", restaurant_id);
                intent3.putExtras(b3);
                a.startActivity(intent3);
            }
            return true;
        } else if(id==R.id.action_reviews){
            if (a instanceof ReviewsActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent4 = new Intent(
                        a.getApplicationContext(),
                        ReviewsActivity.class);
                intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b4 = new Bundle();
                b4.putString("restaurant_id", restaurant_id);
                intent4.putExtras(b4);
                a.startActivity(intent4);
            }
            return true;
        } else if(id==R.id.action_statistics){
            if (a instanceof StatisticsActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent5 = new Intent(
                        a.getApplicationContext(),
                        StatisticsActivity.class);
                intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b5 = new Bundle();
                b5.putString("restaurant_id", restaurant_id);
                intent5.putExtras(b5);
                a.startActivity(intent5);
            }
            return true;
        } else if(id==R.id.action_edit){
            if (a instanceof AddRestaurantActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent6 = new Intent(
                        a.getApplicationContext(),
                        AddRestaurantActivity.class);
                intent6.putExtra("Restaurant", my_restaurant);
                intent6.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                final AppBarLayout appbar = (AppBarLayout) a.findViewById(R.id.appbar);
                appbar.setExpanded(false);
                a.startActivityForResult(intent6, MODIFY_INFO);
            }
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean drawer_owner_not_restaurant_page(Activity a, MenuItem item, String restaurant_id) {
        int MODIFY_INFO = 4;
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) a.findViewById(R.id.drawer_layout);
        if(id==R.id.action_my_restaurants){
            if (a instanceof MainActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent1 = new Intent(
                        a.getApplicationContext(),
                        MainActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                a.startActivity(intent1);
            }
            return true;

        } else if(id==R.id.action_show_as) {
            Intent intent1 = new Intent(
                    a.getApplicationContext(),
                    UserRestaurantActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            Bundle b = new Bundle();
            b.putString("restaurant_id", restaurant_id);
            intent1.putExtras(b);
            a.startActivity(intent1);
            return true;
        } else if(id==R.id.action_gallery) {
            if (a instanceof GalleryViewActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent1 = new Intent(
                        a.getApplicationContext(),
                        GalleryViewActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b = new Bundle();
                b.putString("restaurant_id", restaurant_id);
                intent1.putExtras(b);
                a.startActivity(intent1);
            }
            return true;
        } else if(id==R.id.action_menu) {
            if (a instanceof MenuRestaurant_page ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent1 = new Intent(
                        a.getApplicationContext(),
                        MenuRestaurant_page.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b = new Bundle();
                b.putString("restaurant_id", restaurant_id);
                intent1.putExtras(b);
                a.startActivity(intent1);
            }
            return true;
        } else if(id==R.id.action_offers) {
            if (a instanceof MyOffersActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent2 = new Intent(
                        a.getApplicationContext(),
                        MyOffersActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b2 = new Bundle();
                b2.putString("restaurant_id", restaurant_id);
                intent2.putExtras(b2);
                a.startActivity(intent2);
            }
            return true;
        } else if(id==R.id.action_reservations){
            if (a instanceof ReservationActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent3 = new Intent(
                        a.getApplicationContext(),
                        ReservationActivity.class);
                intent3.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b3 = new Bundle();
                b3.putString("restaurant_id", restaurant_id);
                intent3.putExtras(b3);
                a.startActivity(intent3);
            }
            return true;
        } else if(id==R.id.action_reviews){
            if (a instanceof ReviewsActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent4 = new Intent(
                        a.getApplicationContext(),
                        ReviewsActivity.class);
                intent4.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b4 = new Bundle();
                b4.putString("restaurant_id", restaurant_id);
                intent4.putExtras(b4);
                a.startActivity(intent4);
            }
            return true;
        } else if(id==R.id.action_statistics){
            if (a instanceof StatisticsActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent5 = new Intent(
                        a.getApplicationContext(),
                        StatisticsActivity.class);
                intent5.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                Bundle b5 = new Bundle();
                b5.putString("restaurant_id", restaurant_id);
                intent5.putExtras(b5);
                a.startActivity(intent5);
            }
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static boolean drawer_user_not_restaurant_page(Activity a, MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) a.findViewById(R.id.drawer_layout);
        if(id==R.id.nav_owner){
            Intent intent = new Intent(a, MainActivity.class);
            //from USER to owner I do not want to reset stack: user_restaurant_list must be the only point in which the back produces exit the application
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            a.startActivity(intent);
            return true;
        }
        else if(id==R.id.nav_home){
            if (a instanceof UserRestaurantList ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent = new Intent(a, UserRestaurantList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                a.startActivity(intent);
            }
            return true;
        }
        else if(id==R.id.nav_login){
            if (a instanceof LoginManagerActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent = new Intent(a, LoginManagerActivity.class);
                intent.putExtra("login", true);
                a.startActivity(intent);
            }
            return true;
        } else if(id==R.id.nav_logout){
            if (a instanceof LoginManagerActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent = new Intent(a, LoginManagerActivity.class);
                intent.putExtra("login", false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                a.startActivity(intent);
            }
            return true;
        } else if(id==R.id.nav_my_profile) {
            if (a instanceof UserProfile ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent = new Intent(a, UserProfile.class);
                a.startActivity(intent);
            }
            return true;
        } else if(id==R.id.nav_my_orders) {
            if (a instanceof MyOrdersActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent = new Intent(a, MyOrdersActivity.class);
                a.startActivity(intent);
            }
            return true;
        } else if(id==R.id.nav_my_reservations){
            if (a instanceof UserMyReservations ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent = new Intent(a, UserMyReservations.class);
                a.startActivity(intent);
            }
            return true;
        } else if(id==R.id.nav_my_reviews){
            if (a instanceof MyReviewsActivity ) {
                drawer.closeDrawer(GravityCompat.START);
            }
            else {
                Intent intent = new Intent(a, MyReviewsActivity.class);
                a.startActivity(intent);
            }
            return true;
        } else if(id==R.id.nav_my_favourites){
                if (a instanceof UserMyFavourites ) {
                    drawer.closeDrawer(GravityCompat.START);
                }
                else {
                    Intent intent = new Intent(a, UserMyFavourites.class);
                    a.startActivity(intent);
                }
            return true;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}

