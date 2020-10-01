package com.samoreque.freshlypressed.repository

import android.net.Uri
import android.os.Build
import java.lang.Exception
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.samoreque.freshlypressed.MainCoroutineScopeRule
import com.samoreque.freshlypressed.PostsTestUtils
import com.samoreque.freshlypressed.api.PostsRequestException
import com.samoreque.freshlypressed.api.WordPressApi

import com.samoreque.freshlypressed.respository.NetworkRepository
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config


@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [Build.VERSION_CODES.O_MR1])
class WordPressRepositoryTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @Mock
    lateinit var wordPressApi: WordPressApi

    private lateinit var networkRepository: NetworkRepository

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)
        networkRepository = NetworkRepository(wordPressApi, Dispatchers.Main)

    }

    @Test
    fun `Should return a PostRequest when loadPosts is called`() = runBlockingTest {
        //Arrange
        val postRequestExpected = PostsTestUtils.getPostRequest(5)
        `when`(wordPressApi.loadPosts(any(), any())).thenReturn(postRequestExpected)
        //Act
        val postRequest = networkRepository.loadPosts(5)
        //Assert
        assertThat(postRequest).isEqualTo(postRequestExpected)
    }

    @Test
    fun `Should throw a PostsRequestException when loadPosts is called with error`() = runBlockingTest {
        //Arrange
        val exception = Exception("Error")
        var exceptionActual: Throwable? = null
        `when`(wordPressApi.loadPosts(any(), any())).thenAnswer {
            throw exception
        }
        //Act
        try {
            networkRepository.loadPosts(5)
        } catch (throwable: Throwable) {
            exceptionActual = throwable
        }

        //Assert
        assertThat(exceptionActual is PostsRequestException).isEqualTo(true)

    }

    @Test
    fun `Should return a Site when loadSubscribersCount is called`() = runBlockingTest {
        //Arrange
        val site = PostsTestUtils.getSite(count = 5)
        val url = Uri.parse("http://www.someHost.com")
        `when`(wordPressApi.loadSubscribersCount(any())).thenReturn(site)
        //Act
        val siteActual = networkRepository.loadSubscribersCount(url)
        //Assert
        assertThat(siteActual).isEqualTo(site)
    }

    @Test
    fun `Should not loadSubscribersCount be called when the URL has bad format`() = runBlockingTest {
        //Arrange
        val url = Uri.parse("/www.someHost.com")
        //Act
        val siteActual = networkRepository.loadSubscribersCount(url)
        //Assert
        verify(wordPressApi, never()).loadSubscribersCount(any())
        assertThat(siteActual).isEqualTo(null)
    }
}