package com.example.messengerapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import com.example.messengerapp.Fragments.ChatsFragment
import com.example.messengerapp.Fragments.SearchFragment
import com.example.messengerapp.Fragments.SettingsFragment
import com.example.messengerapp.Model.ModelClasses.Chat
import com.example.messengerapp.Model.ModelClasses.Users
import com.example.messengerapp.Presenter.Presenter
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var refUsers: DatabaseReference? = null
    var firebaseUser: FirebaseUser? = null
    var refUsers_listener: ValueEventListener? = null
    var refChats: DatabaseReference? = null
    var refChats_listener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar_main)

        firebaseUser = FirebaseAuth.getInstance().currentUser
        Log.d("Mainacter", firebaseUser.toString())

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar_main)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = ""

        val tabLayout: TabLayout = findViewById(R.id.tab_layout)
        val viewPager: ViewPager = findViewById(R.id.view_pager)

        // view functionality
        refChats = Presenter.getChild("Chats", null)
        refChats_listener = refChats!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(p0: DataSnapshot) {

                val viewPagerAdapter = ViewPagerAdapter(supportFragmentManager)
                var unreadMessages = 0

                for (d in p0.children) {
                    val chat = d.getValue(Chat::class.java)
                    if (chat!!.getReceiver()
                            .equals(firebaseUser!!.uid) && !chat.isIsSeen()!!
                    ) {
                        unreadMessages += 1
                    }
                }

                if (unreadMessages == 0) {
                    viewPagerAdapter.AddFragment(ChatsFragment(), title = "Chats")
                } else {
                    viewPagerAdapter.AddFragment(
                        ChatsFragment(),
                        title = "Chats ($unreadMessages)"
                    )
                }
                viewPagerAdapter.AddFragment(SearchFragment(), title = "Search")
                viewPagerAdapter.AddFragment(SettingsFragment(), title = "Settings")
                viewPager.adapter = viewPagerAdapter
                tabLayout.setupWithViewPager(viewPager)
            }

        })

        //display username and profile picture
        refUsers =
            FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)
        refUsers_listener = refUsers!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    val user: Users? = p0.getValue(Users::class.java)
                    user_name.text = user!!.getUserName()
                    Picasso.get().load(user.getProfile()).placeholder(R.drawable.profile)
                        .into(profile_image)
                }
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        when (item.itemId) {
            R.id.action_logout -> {
                refChats!!.removeEventListener(refChats_listener!!)
                refUsers!!.removeEventListener(refUsers_listener!!)
                val intent = Intent(this@MainActivity, WelcomeActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
                FirebaseAuth.getInstance().signOut()
                return true
            }
        }
        return false
    }

    internal class ViewPagerAdapter(FragmentManager: FragmentManager) :
        FragmentPagerAdapter(FragmentManager) {
        private val fragments: ArrayList<Fragment>
        private val titles: ArrayList<String>

        init {
            fragments = ArrayList<Fragment>()
            titles = ArrayList<String>()
        }

        override fun getItem(position: Int): Fragment {
            return fragments[position]
        }

        override fun getCount(): Int {
            return fragments.size
        }

        fun AddFragment(fragment: Fragment, title: String) {
            fragments.add(fragment)
            titles.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return titles[position]
        }
    }

    override fun onResume() {
        super.onResume()
        Presenter.updateStatus("online")
    }

    override fun onPause() {
        super.onPause()
        Presenter.updateStatus("oflline")
    }
}
