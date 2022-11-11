package com.ll.ebookmarket.app.post.service;

import com.ll.ebookmarket.app.member.entity.Member;
import com.ll.ebookmarket.app.myBook.service.MyBookService;
import com.ll.ebookmarket.app.post.entity.Post;
import com.ll.ebookmarket.app.post.repository.PostRepository;
import com.ll.ebookmarket.app.postTag.entity.PostTag;
import com.ll.ebookmarket.app.postTag.service.PostTagService;
import com.ll.ebookmarket.app.product.entity.Product;
import com.ll.ebookmarket.app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final PostTagService postTagService;
    private final MyBookService myBookService;
    private final ProductService productService;

    @Transactional
    public Post write(Member author, String subject, String content, String contentHtml, String postTagContents) {
        Post post = Post.builder()
                .subject(subject)
                .content(content)
                .contentHtml(contentHtml)
                .author(author)
                .build();
        postRepository.save(post);

        applyPostTags(post, postTagContents);

        return post;
    }

    public Optional<Post> findById(long postId) {
        return postRepository.findById(postId);
    }

    public void applyPostTags(Post post, String postTagContents) {
        postTagService.applyPostTags(post, postTagContents);
    }

    public List<PostTag> getPostTags(Post post) {
        return postTagService.getPostTags(post);
    }

    public void modify(Post post, String subject, String content, String contentHtml, String postTagContents) {
        post.setSubject(subject);
        post.setContent(content);
        post.setContentHtml(contentHtml);
        applyPostTags(post, postTagContents);
    }

    public boolean actorCanModify(Member author, Post post) {
        return author.getId().equals(post.getAuthor().getId());
    }

    public boolean actorCanRemove(Member author, Post post) {
        return actorCanModify(author, post);
    }

    public List<PostTag> getPostTags(Member author, String postKeywordContent) {
        List<PostTag> postTags = postTagService.getPostTags(author, postKeywordContent);

        loadForPrintDataOnPostTagList(postTags);

        return postTags;
    }

    public Optional<Post> findForPrintById(long id) {
        Optional<Post> opPost = findById(id);

        if (opPost.isEmpty()) return opPost;

        List<PostTag> postTags = getPostTags(opPost.get());

        opPost.get().getExtra().put("postTags", postTags);

        return opPost;
    }

    private void loadForPrintDataOnPostTagList(List<PostTag> postTags) {
        List<Post> posts = postTags
                .stream()
                .map(PostTag::getPost)
                .collect(toList());

        loadForPrintData(posts);
    }

    public void loadForPrintData(List<Post> posts) {
        long[] ids = posts
                .stream()
                .mapToLong(Post::getId)
                .toArray();

        List<PostTag> postTagsByPostIds = postTagService.getPostTagsByPostIdIn(ids);

        Map<Long, List<PostTag>> postTagsByPostIdsMap = postTagsByPostIds.stream()
                .collect(groupingBy(
                        postTag -> postTag.getPost().getId(), toList()
                ));

        posts.stream().forEach(post -> {
            List<PostTag> postTags = postTagsByPostIdsMap.get(post.getId());

            if (postTags == null || postTags.size() == 0) return;

            post.getExtra().put("postTags", postTags);
        });
    }

    public List<Post> findAllForPrintByAuthorIdOrderByIdDesc(long authorId) {
        List<Post> posts = postRepository.findAllByAuthorIdOrderByIdDesc(authorId);
        loadForPrintData(posts);

        return posts;
    }

    public void remove(Post post) {
        postRepository.delete(post);
    }

    public boolean actorCanSee(Member actor, Post post) {
        if ( actor == null ) return false;
        if ( post == null ) return false;

        List<Product> products = myBookService.getProductsByMember(actor);
        Optional<Post> any = products.stream().map(productService::findPostsByProduct)
                .flatMap(List::stream)
                .filter(post1 -> post1.equals(post))
                .findAny();

        if(any.isPresent()) {
            return true;
        }

        return post.getAuthor().getId().equals(actor.getId());
    }
}
