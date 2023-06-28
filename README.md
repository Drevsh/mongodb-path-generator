<a name="readme-top"></a>

<!-- PROJECT LOGO -->
<br />
<div align="center">
  <a href="https://github.com/github_username/repo_name">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">MongoDB Path Generator</h3>
  <p align="center">
    project_description
    <br />
    <a href="https://github.com/github_username/repo_name">View Demo</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Report Bug</a>
    ·
    <a href="https://github.com/github_username/repo_name/issues">Request Feature</a>
  </p>
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li>
      <a href="#about-the-project">About The Project</a>
      <ul>
        <li><a href="#built-with">Built With</a></li>
      </ul>
    </li>
    <li>
      <a href="#getting-started">Getting Started</a>
      <ul>
        <li><a href="#prerequisites">Prerequisites</a></li>
        <li><a href="#installation">Installation</a></li>
      </ul>
    </li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#roadmap">Roadmap</a></li>
    <li><a href="#contributing">Contributing</a></li>
    <li><a href="#license">License</a></li>
    <li><a href="#contact">Contact</a></li>
    <li><a href="#acknowledgments">Acknowledgments</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->

## About The Project

The MongoDB Path Generator for MongoDB is a versatile tool designed to facilitate the creation of accurate paths that can be utilized by the MongoDB client to filter and fetch data. This project aims to simplify the process of constructing precise queries for MongoDB databases, ensuring the retrieval of the desired information.

Constructing correct paths is crucial to effectively query MongoDB databases and retrieve accurate results. The MongoDB Path Generator provides a user-friendly interface for developers to define and create paths that adhere to the specific structure and schema of their MongoDB documents.

The MongoDB Path Generator for MongoDB empowers developers to construct accurate and efficient queries by providing a user-friendly interface and leveraging the capabilities of Annotation Processing and the MongoDB client. It ensures that the paths adhere to the document's structure, enabling precise data retrieval and reducing the risk of incorrect results.

With the MongoDB Path Generator, developers can save time and effort by automating the path generation process and avoiding potential errors. It simplifies the development workflow and enhances the overall accuracy and efficiency of queries executed against MongoDB databases.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

### Built With

- [Java](https://www.java.com/en/)
- [Maven](https://maven.apache.org/)
- [JavaPoet](https://github.com/square/javapoet)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- GETTING STARTED -->

## Getting Started

This is an example of how you may give instructions on setting up your project locally.
To get a local copy up and running follow these simple example steps.

<!-- USAGE EXAMPLES -->

## Usage

Annotate you entites with `@Entity` and properties or fields with `@Property`.

```
@Entity
public interface Label extends BaseEntity<String>{
    @Property
    default String description() {
        return Constants.NOT_AVAILABLE;
    }

    @Property
    default int count() {
        return 0;
    }
}

public interface BaseEntity<T> {
    @Property
    T id();

    @Property
    default Instant creationDate() {
        return Instant.now();
    }

    @Property
    default Instant modifiedDate() {
        return Instant.now();
    }
}
```

After running the annotation processor thw following file will be generated:

```
@Generated(
    date = "2023-06-28T11:41:13.732915600",
    value = "de.stahlmann.metamodel.processor.MetamodelProcessor"
)
public final class Label_ extends Path {
    public Label_(String subgraph) {
        path = subgraph;
    }

    public static Label_ root() {
        return new Label_("");
    }

    public String count() {
        add("count");
        return getPath();
    }

    public String description() {
        add("description");
        return getPath();
    }

    public String id() {
        add("id");
        return getPath();
    }

    public String creationDate() {
        add("creationDate");
        return getPath();
    }

    public String modifiedDate() {
        add("modifiedDate");
        return getPath();
    }
}
```

To generate the appropriate path simply use the `root()` method to generate an appropriate path:

```
    var path = Label_.root()
      .id();
    // path = "id"
```

For single uses this is not really useful but if you have more complex structures the benefit directly comes apparent.

Example:

```
public interface Document extends BaseEntity<String> {
    @Property
    String version();

    @Property
    default Collection<LabelEntity> labels() {
        return List.of();
    }
}
```

The generated `Document_.java` files looks as follows:

```
public final class Document_ extends Path {
    public DocumentEntity_(String subgraph) {
        path = subgraph;
    }

    public static Document_ root() {
        return new Document_("");
    }

    public String id() {
        add("id");
        return getPath();
    }

    public String creationDate() {
        add("creationDate");
        return getPath();
    }

    public String modifiedDate() {
        add("modifiedDate");
        return getPath();
    }

    public LabelEntity_ labels() {
        add("labels");
        return new LabelEntity_(path);
    }
}
```

Which then can be used for correct path creation to sub resources:

```
    var path = Document_.root()
        .labels()
        .id();
    // path = "labels.id"
```

If now any of the methods on the entity classes are renamed the compiler will throw an error, since the methods are not available on the metamodel classes anymore.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTRIBUTING -->

## Contributing

Contributions are what make the open source community such an amazing place to learn, inspire, and create. Any contributions you make are **greatly appreciated**.

If you have a suggestion that would make this better, please fork the repo and create a pull request. You can also simply open an issue with the tag "enhancement".
Don't forget to give the project a star! Thanks again!

1. Fork the Project
2. Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3. Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the Branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- LICENSE -->

## License

Distributed under the MIT License. See `LICENSE.txt` for more information.

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- CONTACT -->

## Contact

Stephan Stahlmann - drevsh.stahlmann@gmail.com

Project Link: [https://github.com/github_username/repo_name](https://github.com/github_username/repo_name)

<p align="right">(<a href="#readme-top">back to top</a>)</p>

<!-- ACKNOWLEDGMENTS -->

<!-- MARKDOWN LINKS & IMAGES -->
<!-- https://www.markdownguide.org/basic-syntax/#reference-style-links -->

[contributors-shield]: https://img.shields.io/github/contributors/github_username/repo_name.svg?style=for-the-badge
[contributors-url]: https://github.com/github_username/repo_name/graphs/contributors
[forks-shield]: https://img.shields.io/github/forks/github_username/repo_name.svg?style=for-the-badge
[forks-url]: https://github.com/github_username/repo_name/network/members
[stars-shield]: https://img.shields.io/github/stars/github_username/repo_name.svg?style=for-the-badge
[stars-url]: https://github.com/github_username/repo_name/stargazers
[issues-shield]: https://img.shields.io/github/issues/github_username/repo_name.svg?style=for-the-badge
[issues-url]: https://github.com/github_username/repo_name/issues
[license-shield]: https://img.shields.io/github/license/github_username/repo_name.svg?style=for-the-badge
[license-url]: https://github.com/github_username/repo_name/blob/master/LICENSE.txt
[linkedin-shield]: https://img.shields.io/badge/-LinkedIn-black.svg?style=for-the-badge&logo=linkedin&colorB=555
[linkedin-url]: https://linkedin.com/in/linkedin_username
[product-screenshot]: images/screenshot.png
