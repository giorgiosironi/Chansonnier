package org.eclipse.smila.ontology.internal;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.smila.management.ManagementAgent;
import org.eclipse.smila.ontology.SesameOntologyManager;
import org.openrdf.model.Namespace;
import org.openrdf.model.Resource;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFWriter;
import org.openrdf.rio.RDFWriterFactory;
import org.openrdf.rio.RDFWriterRegistry;
import org.openrdf.rio.rdfxml.util.RDFXMLPrettyWriter;

/**
 * Management agent for the Sesame Ontology service. Use to import/export RDF files into repositories, clear
 * repositories or get statistical information about repositories.
 *
 * @author jschumacher
 *
 */
public class SesameOntologyAgent implements ManagementAgent {
  /**
   * local logger.
   */
  private final Log _log = LogFactory.getLog(getClass());

  /**
   * managed ontology manager.
   */
  private final SesameOntologyManager _ontology;

  /**
   * create instance.
   *
   * @param ontology
   *          managed ontology manager.
   */
  public SesameOntologyAgent(final SesameOntologyManager ontology) {
    _ontology = ontology;
  }

  /**
   *
   * @return names of configured repositories.
   */
  public List<String> getRepositoryNames() {
    return _ontology.getRepositoryNames();
  }

  /**
   * get number of statements in repository.
   *
   * @param repositoryName
   *          name of repository.
   * @return number of statements in repository, or -1 if no repository with this name exists
   */
  public long getSize(final String repositoryName) {
    long size = -1;
    try {
      final RepositoryConnection conn = _ontology.getConnection(repositoryName);
      if (conn != null) {
        try {
          size = conn.size();
        } finally {
          closeQuietly(conn);
        }
      }
    } catch (final Exception ex) {
      _log.error("Error getting size of repository " + repositoryName, ex);
    }
    return size;
  }

  /**
   * get context IDs of repository.
   *
   * @param repositoryName
   *          name of repository.
   * @return context names in repository, of an empty list if no contexts are defined.
   */
  public List<String> getContexts(final String repositoryName) {
    final List<String> result = new ArrayList<String>();
    try {
      final RepositoryConnection conn = _ontology.getConnection(repositoryName);
      if (conn != null) {
        try {
          final RepositoryResult<Resource> contexts = conn.getContextIDs();
          if (contexts != null) {
            while (contexts.hasNext()) {
              final Resource context = contexts.next();
              result.add(context.stringValue());
            }
          }
        } finally {
          closeQuietly(conn);
        }
      }
    } catch (final Exception ex) {
      _log.error("Error getting contexts of repository " + repositoryName, ex);
    }
    return result;

  }

  /**
   * get namespaces defined in repository.
   *
   * @param repositoryName
   *          name of repository.
   * @return map of namespace prefixes to URIs, of an empty map if no contexts are defined.
   */
  public Map<String, String> getNamespaces(final String repositoryName) {
    final Map<String, String> result = new HashMap<String, String>();
    try {
      final RepositoryConnection conn = _ontology.getConnection(repositoryName);
      if (conn != null) {
        try {
          final RepositoryResult<Namespace> namespaces = conn.getNamespaces();
          if (namespaces != null) {
            while (namespaces.hasNext()) {
              final Namespace namespace = namespaces.next();
              result.put(namespace.getPrefix(), namespace.getName());
            }
          }
        } finally {
          closeQuietly(conn);
        }
      }
    } catch (final Exception ex) {
      _log.error("Error getting namespaces of repository " + repositoryName, ex);
    }
    return result;
  }

  /**
   * clear repository. This removes all statements. Other data (namespaces etc.) may remain.
   *
   * @param repositoryName
   *          name of repository.
   * @return message to display.
   */
  public String clear(final String repositoryName) {
    try {
      final RepositoryConnection conn = _ontology.getConnection(repositoryName);
      if (conn != null) {
        try {
          final long startSize = conn.size();
          conn.clear();
          conn.commit();
          final long endSize = conn.size();
          return "Cleared repository [" + repositoryName + "], deleted " + (startSize - endSize) + " statements, "
            + endSize + " statements remain.";
        } finally {
          closeQuietly(conn);
        }
      }
      return "No repository named [" + repositoryName + "] exists.";
    } catch (final Exception ex) {
      return "Error: " + ex.toString();
    }

  }

  /**
   * import RDF file into repository.
   *
   * @param repositoryName
   *          name of repository
   * @param filename
   *          path to RDF file. Must be absolute or relative to working directory of SMILA runtime.
   * @param baseUri
   *          base URI of ontology (default namespace), used to resolve relative URIs in RDF file. Should always be
   *          specified, or unexptected results may occur.
   * @return message to display.
   */
  public String importRDF(final String repositoryName, final String filename, final String baseUri) {
    try {
      final RepositoryConnection conn = _ontology.getConnection(repositoryName);
      if (conn != null) {
        try {
          final long startSize = conn.size();
          final RDFFormat format = RDFFormat.forFileName(filename, RDFFormat.RDFXML);
          conn.add(new File(filename), baseUri, format);
          conn.commit();
          final long endSize = conn.size();
          return "Imported [" + filename + "] (format " + format.getName() + ") into repository [" + repositoryName
            + "], added " + (endSize - startSize) + " statements.";
        } finally {
          closeQuietly(conn);
        }
      }
      return "No repository named [" + repositoryName + "] exists.";
    } catch (final Exception ex) {
      return "Error: " + ex.toString();
    }
  }

  /**
   * export ontology contents as RDF file.
   *
   * @param repositoryName
   *          name of repository
   * @param filename
   *          path to RDF file. Must be absolute or relative to working directory of SMILA runtime.
   * @return message to display.
   */
  public String exportRDF(final String repositoryName, final String filename) {
    RepositoryConnection conn = null;
    FileOutputStream stream = null;
    try {
      conn = _ontology.getConnection(repositoryName);
      if (conn != null) {
        stream = new FileOutputStream(filename);
        final RDFFormat format = RDFFormat.forFileName(filename, RDFFormat.RDFXML);
        final RDFWriterFactory factory = RDFWriterRegistry.getInstance().get(format);
        RDFWriter writer = null;
        if (factory == null) {
          writer = new RDFXMLPrettyWriter(stream);
        } else {
          writer = factory.getWriter(stream);
        }

        conn.export(writer);
        if (writer instanceof RDFXMLPrettyWriter) {
          ((RDFXMLPrettyWriter) writer).close();
        } else {
          stream.close();
        }
        return "Successfully exported repository [" + repositoryName + "] to file [" + filename + "] (format "
          + format.getName() + ")";
      }
      return "No repository named [" + repositoryName + "] exists.";
    } catch (final Exception ex) {
      return "Error: " + ex.toString();
    } finally {
      IOUtils.closeQuietly(stream);
      closeQuietly(conn);
    }

  }

  /**
   * close a connection (if there is one) without throwing an exception.
   *
   * @param conn
   *          connection to close. may be null.
   */
  private void closeQuietly(final RepositoryConnection conn) {
    if (conn != null) {
      try {
        conn.close();
      } catch (final RepositoryException e) {
        ; // ignore
      }
    }
  }

}
