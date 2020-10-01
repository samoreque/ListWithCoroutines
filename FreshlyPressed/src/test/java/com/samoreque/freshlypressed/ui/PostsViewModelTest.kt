package com.samoreque.freshlypressed.ui

import android.net.Uri
import android.os.Build

import java.lang.Exception

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest

import com.samoreque.freshlypressed.respository.WordPressRepository
import com.samoreque.freshlypressed.MainCoroutineScopeRule
import com.samoreque.freshlypressed.PostsTestUtils
import com.samoreque.freshlypressed.api.PostsRequestException
import com.samoreque.freshlypressed.exceptions.LoadMoreException
import org.assertj.core.api.Assertions.assertThat
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.never
import com.nhaarman.mockitokotlin2.verify
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
class PostsViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineScope = MainCoroutineScopeRule()

    @Mock
    lateinit var repository: WordPressRepository

    private lateinit var viewModel: PostsViewModel

    @Before
    fun setUp() {

        MockitoAnnotations.initMocks(this)
        viewModel = PostsViewModel(repository)

    }

    @Test
    fun `Should return the posts when loadPosts is called`() = runBlockingTest {
        //Arrange
        val postRequest = PostsTestUtils.getPostRequest(5)
        `when`(repository.loadPosts(any(), any())).thenReturn(postRequest)

        //Act
        viewModel.loadPosts()

        //Assert
        assertThat(viewModel.postList.value).isEqualTo(postRequest.posts)
        assertThat(viewModel.error.value).isEqualTo(null)
    }

    @Test
    fun `Should set error when loadPosts is called with error`() = runBlockingTest {
        //Arrangee
        `when`(repository.loadPosts(any(), any())).thenAnswer {
            throw PostsRequestException(Exception())
        }

        //Act
        viewModel.loadPosts()

        //Assert
        assertThat(viewModel.postList.value).isEqualTo(null)
        assertThat(viewModel.error.value is PostsRequestException).isEqualTo(true)
    }

    @Test
    fun `Should disable the loading spinner when the loadPosts finished`() = runBlockingTest {
        //Arrange
        val postRequest = PostsTestUtils.getPostRequest(5)
        `when`(repository.loadPosts(any(), any())).thenReturn(postRequest)

        //Act
        viewModel.loadPosts()

        //Assert
        assertThat(viewModel.spinner.value).isEqualTo(false)
    }

    @Test
    fun `Should set postUpdate when loadSubscribersCount is called`() = runBlockingTest {
        //Arrange
        val site = PostsTestUtils.getSite(count = 10)
        val url = Uri.parse("http://www.someHost.com")
        val position = 10
        `when`(repository.loadSubscribersCount(any())).thenReturn(site)

        //Act
        viewModel.loadSubscribersCount(position, url)

        //Assert
        assertThat(viewModel.postUpdated.value).isEqualTo(position)
        assertThat(viewModel.subscriberCache[url.host]).isEqualTo(site)
    }

    @Test
    fun `Should not set postUpdate when loadSubscribersCount is called with error`() = runBlockingTest {
        //Arrange
        val url = Uri.parse("http://www.someHost.com")
        `when`(repository.loadSubscribersCount(any())).thenAnswer {
            throw Exception()
        }

        //Act
        viewModel.loadSubscribersCount(10, url)

        //Assert
        assertThat(viewModel.postUpdated.value).isEqualTo(null)
        assertThat(viewModel.subscriberCache[url.host]).isEqualTo(null)
    }

    @Test
    fun `Shouldn't call repository when the Site data is in the cache`() = runBlockingTest {
        //Arrange
        val site = PostsTestUtils.getSite(count = 10)
        val url = Uri.parse("http://www.someHost.com")
        val position = 10

        //Act
        viewModel.subscriberCache[url.host!!] = site
        val countActual = viewModel.loadSubscribersCount(position, url)

        //Assert
        verify(repository, never()).loadSubscribersCount(any())
        assertThat(viewModel.postUpdated.value).isEqualTo(null)
        assertThat(countActual).isEqualTo(site.subscribersCount.toString())
    }

    @Test
    fun `Should append the new posts when loadMore is called`() = runBlockingTest {
        //Arrange
        val postRequestPrevious = PostsTestUtils.getPostRequest(5)
        val postRequest = PostsTestUtils.getPostRequest(5)
        val postsExpected = postRequestPrevious.posts + postRequest.posts
        viewModel.postsRequest = postRequestPrevious
        `when`(repository.loadPosts(any(), any())).thenReturn(postRequest)

        //Act
        viewModel.onLoadMore()

        //Assert
        assertThat(viewModel.postList.value).isEqualTo(postsExpected)
        assertThat(viewModel.error.value).isEqualTo(null)
    }

    @Test
    fun `Should throw LoadMoreException when loadMore failed`() = runBlockingTest {
        //Arrange
        `when`(repository.loadPosts(any(), any())).thenAnswer{
            throw Exception()
        }

        //Act
        viewModel.onLoadMore()

        //Assert
        assertThat(viewModel.error.value is LoadMoreException).isEqualTo(true)
    }

    @Test
    fun `Should disable the refreshSpinner when refresh post finished`() = runBlockingTest {
        //Arrange
        val postRequest = PostsTestUtils.getPostRequest(5)
        `when`(repository.loadPosts(any(), any())).thenReturn(postRequest)

        //Act
        viewModel.refreshPost()

        //Assert
        assertThat(viewModel.refreshSpinner.value).isEqualTo(false)
    }

}