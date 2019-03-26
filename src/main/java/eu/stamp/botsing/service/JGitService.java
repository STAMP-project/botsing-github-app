package eu.stamp.botsing.service;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CheckoutCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.CreateBranchCommand;
import org.eclipse.jgit.api.CreateBranchCommand.SetupUpstreamMode;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.InvalidRemoteException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.io.Files;

import eu.stamp.botsing.utility.ConfigurationBean;

@Service
public class JGitService {
	UsernamePasswordCredentialsProvider upCredentialsProvider;

	Logger log = LoggerFactory.getLogger(JGitService.class);

	@Autowired
	public JGitService(ConfigurationBean configuration) {
		super();

		if (configuration.getGithubUsername() != null && configuration.getGithubPassword() != null) {
			log.debug("Connecting to GitHub as: " + configuration.getGithubUsername());
			upCredentialsProvider = new UsernamePasswordCredentialsProvider(configuration.getGithubUsername(),
					configuration.getGithubPassword());

		} else {
			log.debug("No user configured to Connect to GitHub.");
		}
	}

	public Repository getRepository(File repositoryFolder) throws IOException {
		return new FileRepositoryBuilder().readEnvironment().findGitDir(repositoryFolder).build();
	}

	/**
	 * Clone the repository in the folder specified. If the folder is null, it will
	 * use a temp folder.
	 *
	 * @param repositoryURL
	 * @param repositoryFolder
	 * @return
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	public File cloneRepository(String repositoryURL, File repositoryFolder)
			throws InvalidRemoteException, TransportException, GitAPIException {
		log.debug("Cloning repository"
				+ (repositoryFolder != null ? (" in " + repositoryFolder.getAbsolutePath()) : "..."));

		if (repositoryFolder == null) {
			repositoryFolder = Files.createTempDir();
		}

		TransportCommand command = Git.cloneRepository().setURI(repositoryURL).setDirectory(repositoryFolder);
		gitCommandCall(command);

		log.debug("Repository cloned in " + repositoryFolder.getAbsolutePath());

		return repositoryFolder;
	}

	/**
	 * Clone the repository in a temp folder
	 *
	 * @param repositoryURL
	 * @return
	 * @throws InvalidRemoteException
	 * @throws TransportException
	 * @throws GitAPIException
	 */
	public File cloneRepository(String repositoryURL)
			throws InvalidRemoteException, TransportException, GitAPIException {

		return cloneRepository(repositoryURL, null);
	}

	public Repository checkoutBranch(File repositoryFolder, String branch) throws Exception {
		log.debug("Creating new branch " + branch);

		CheckoutCommand checkout;
		Git git = null;

		try {
			Repository repository = new FileRepositoryBuilder().readEnvironment().findGitDir(repositoryFolder).build();
			git = new Git(repository);
			checkout = git.checkout();

			String currentBranch = repository.getBranch();
			if (currentBranch.equals(branch)) {
				log.warn("Already in branch '" + branch + "', nothing to do.");
				return repository;
			}

			try {
				// get branch reference
				Ref branchRef = getRef(branch, repository);

				if (branchRef != null) {

					// switch to branch
					checkout.setName(branchRef.getName());
					checkout.call();

					log.debug("New branch '" + branch + "' created");

				} else {
					throw new Exception("Branch '" + branch + "' not found!");
				}

				return repository;

			} catch (Exception e) {
				throw new Exception("Could not checkout repository " + repository.getDirectory().getAbsolutePath(), e);
			}

		} catch (IOException e) {
			throw new Exception("Could not access repository " + repositoryFolder.getAbsolutePath(), e);

		} finally {
			if (git != null) {
				git.close();
			}
		}
	}

	private Ref getRef(String branch, Repository repository) {
		Ref result = null;

		for (Set<Ref> refs : repository.getAllRefsByPeeledObjectId().values()) {
			for (Ref ref : refs) {
				if (ref.getName().endsWith(branch)) {
					result = ref;
					break;
				}
			}
		}

		return result;
	}

	public Repository createNewBranch(File repositoryFolder, String branch, boolean force) throws Exception {
		log.debug("Creating new branch " + branch);

		CreateBranchCommand bcc;
		CheckoutCommand checkout;
		Git git = null;

		try {
			Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repositoryFolder).build();
			git = new Git(repo);

			bcc = git.branchCreate();
			checkout = git.checkout();

			String currentBranch = repo.getBranch();
			if (currentBranch.equals(branch)) {
				log.warn("Already in branch '" + branch + "', nothing to do.");
				return repo;
			}

			try {
				// create branch
				bcc.setName(branch).setUpstreamMode(SetupUpstreamMode.SET_UPSTREAM).setForce(force).call();

				// switch to branch
				checkout.setName(branch);
				checkout.call();
				log.debug("New branch created");

				return repo;

			} catch (Exception e) {
				throw new Exception("Could not checkout repository " + repositoryFolder, e);
			}

		} catch (IOException e) {
			throw new Exception("Could not access repository " + repositoryFolder, e);

		} finally {
			if (git != null) {
				git.close();
			}
		}
	}

	public Repository addFolder(File repositoryFolder, String folder) throws Exception {
		log.debug("Commiting");

		Git git = null;

		try {
			Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repositoryFolder).build();
			git = new Git(repo);

			AddCommand add = git.add();
			add.addFilepattern(folder);

			add.call();
			log.debug("Add folder done");

			return repo;

		} catch (IOException e) {
			throw new Exception("Could not access repository " + repositoryFolder, e);

		} finally {
			if (git != null) {
				git.close();
			}
		}
	}

	public Repository commitAll(File repositoryFolder, String message) throws Exception {
		log.debug("Commiting");

		// git commit -a -m "added tests"
		Git git = null;

		try {
			Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repositoryFolder).build();
			git = new Git(repo);

			CommitCommand commit = git.commit();

			commit.setMessage(message);
			commit.setAll(true);

			commit.call();
			log.debug("Commit done");

			return repo;

		} catch (IOException e) {
			throw new Exception("Could not access repository " + repositoryFolder, e);

		} finally {
			if (git != null) {
				git.close();
			}
		}
	}

	public Repository push(File repositoryFolder, String branch) throws Exception {
		log.debug("Pushing");
		Git git = null;

		try {
			Repository repo = new FileRepositoryBuilder().readEnvironment().findGitDir(repositoryFolder).build();
			git = new Git(repo);

			PushCommand push = git.push();

			// Add branch reference
			if (branch != null) {
				push.add(branch);
			}

			gitCommandCall(push);
			log.debug("Push done");

			return repo;

		} catch (Exception e) {
			throw new Exception("Could not access repository " + repositoryFolder, e);

		} finally {
			if (git != null) {
				git.close();
			}
		}
	}

	private void gitCommandCall(TransportCommand command) throws GitAPIException {
		// Only used in commands that use a Transport where could be used credentials:
		// CloneCommand, FetchCommand, LsRemoteCommand, PullCommand, PushCommand,
		// SubmoduleAddCommand, SubmoduleUpdateCommand

		if (upCredentialsProvider != null) {
			command.setCredentialsProvider(upCredentialsProvider).call();
		} else {
			command.call();
		}

	}

}
