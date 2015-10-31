package org.opencds.terminology.apelon;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.opencds.common.exceptions.OpenCDSRuntimeException;

import com.apelon.apelonserver.client.ApelonException;
import com.apelon.apelonserver.client.ServerConnection;
import com.apelon.apelonserver.client.ServerConnectionSecureSocket;
import com.apelon.dts.client.DTSException;
import com.apelon.dts.client.association.AssociationQuery;
import com.apelon.dts.client.association.AssociationType;
import com.apelon.dts.client.attribute.DTSPropertyType;
import com.apelon.dts.client.concept.ConceptAttributeSetDescriptor;
import com.apelon.dts.client.concept.ConceptChild;
import com.apelon.dts.client.concept.DTSConcept;
import com.apelon.dts.client.concept.DTSSearchOptions;
import com.apelon.dts.client.concept.NavChildContext;
import com.apelon.dts.client.concept.NavQuery;
import com.apelon.dts.client.concept.SearchQuery;
import com.apelon.dts.client.namespace.Namespace;
import com.apelon.dts.client.namespace.NamespaceQuery;
import com.apelon.dts.server.AssociationServer;
import com.apelon.dts.server.DTSConceptServer;
import com.apelon.dts.server.NamespaceServer;
import com.apelon.dts.server.NavQueryServer;
import com.apelon.dts.server.OntylogConceptServer;
import com.apelon.dts.server.SearchQueryServer;
import com.apelon.dts.server.SubsetServer;

/**
 * ApelonDtsUtilityImpl
 * 
 * Implements {@link OpenCDSApelonDtsClient}
 * 
 * Manages the connection to and use of Apelon in retrieving concepts by id,
 * name, parent or contained property.
 * 
 * @author Tyler Tippetts
 * 
 */
public class OpenCDSApelonDtsClient implements ApelonDtsClient {
    private static Log log = LogFactory.getLog(OpenCDSApelonDtsClient.class);

    private final String host;
    private final int port;
    private final String username;
    private final String password;
    private final int nsId;
    private ConceptAttributeSetDescriptor casd;
    
    /**
     * Tests connection specified in UserHome/.opencds/opencds-apelon.properties
     * Checks if namespace exists
     * @param namespace (e.g. "OpenCDS")
     * @param requestParamParser 
     * @throws Exception if connection parameters invalid or namespace not found
     */
    public OpenCDSApelonDtsClient(String host, int port, String username, String password, String namespace) throws Exception {
        if (host == null) {
            throw new OpenCDSRuntimeException("OpenCDSApelonDtsClient requires 'host' url (eg. localhost).");
        }
        if (port == 0) {
            throw new OpenCDSRuntimeException("OpenCDSApelonDtsClient requires 'port' (eg. 6666).");
        }
        if (username == null) {
            throw new OpenCDSRuntimeException("OpenCDSApelonDtsClient requires 'username'");
        }
        if (password == null) {
            throw new OpenCDSRuntimeException("OpenCDSApelonDtsClient requires 'password'");
        }
        if (namespace == null) {
            throw new OpenCDSRuntimeException("OpenCDSApelonDtsClient requires 'namespace' name");
        }
        
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        
        // Test connection parameters (will throw exception if invalid)
        getSecureSocketConnection().close();
        
        this.nsId = findNamespaceId(namespace);
        this.casd = new ConceptAttributeSetDescriptor("casd");
        casd.setAllPropertyTypes(true);
    }

    /**
     * Finds a concept within apelon namespace, searching by name
     * 
     * @param name
     * @return {@link DTSConcept} or null if not found
     * @throws Exception on failed connection or Apelon internal error
     */
    @Override
    public DTSConcept findConceptByName(String name) throws ApelonClientException {
        ServerConnection conn = null;
        try {
            conn = getSecureSocketConnection();
            DTSConcept concept = SearchQuery.createInstance(conn).findConceptByName(name, nsId, casd);
            return concept;
        } catch (ApelonException | ClassNotFoundException | DTSException e) {
            throw new ApelonClientException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(),e);
                }
            }
        }
    }

    /**
     * Finds a concept within apelon namespace, searching by code
     * 
     * @param code
     * @return {@link DTSConcept} or null if not found
     * @throws Exception on failed connection or Apelon internal error
     */
    @Override
    public DTSConcept findConceptByCode(String code) throws ApelonClientException {
        ServerConnection conn = null;
        try {
            conn = getSecureSocketConnection();
            DTSConcept concept = SearchQuery.createInstance(conn).findConceptByCode(code, nsId, casd);
            return concept;
        } catch (ApelonException | ClassNotFoundException | DTSException | IllegalArgumentException e) {
            throw new ApelonClientException(e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(),e);
                }
            }
        }
    }

    /**
     * Finds concepts that are direct children of parentConcept
     * </br>(Convenience method setting maxLevelsDeep to 1 in findDescendantsOfConcept() method)
     * 
     * @param parentConcept found using findConceptByName() or findConceptByCode() methods
     * @param includeParent default is false
     * @return Set of DTSConcept with no duplicates or empty set if none found. Never null.
     * @throws ApelonClientException 
     * @throws Exception on failed connection or Apelon internal error
     */
    @Override
    public Set<DTSConcept> findDirectChildrenOfConcept(DTSConcept parentConcept, boolean includeParent) throws ApelonClientException {
        return findDescendantsOfConcept(parentConcept, 1, includeParent);
    }

    /**
     * Finds descendant concepts of rootConcept through maxLevelsDeep
     * 
     * @param rootConcept found using findConceptByName() or findConceptByCode() methods
     * @param maxLevelsDeep min = 1, max = 50
     * @param includeRoot default is false
     * @return Set of DTSConcept with no duplicates or empty set if none found. Never null.
     * @throws Exception on failed connection or Apelon internal error
     */
    @Override
    public Set<DTSConcept> findDescendantsOfConcept(DTSConcept rootConcept, int maxLevelsDeep, boolean includeRoot)
            throws ApelonClientException {
        if(maxLevelsDeep < 1 || maxLevelsDeep > 50) {
            throw new ApelonClientException(maxLevelsDeep + " is invalid for maxLevelsDeep (1-50)");
        }
        ServerConnection conn = null;
        try {
            conn = getSecureSocketConnection();
        	// Query apelon for Association Type
            AssociationType associationType = AssociationQuery.createInstance(conn).findAssociationTypeByName(
                    "Parent Of", nsId);
            // Get all children concepts recursively - does not fetch properties
            Set<ConceptChild> children = getConceptChildren(rootConcept, associationType, conn, maxLevelsDeep);
            // Get codes and a set of namespace ids for second query to get properties
            String[] codes = new String[children.size()];
            int[] nsIds = new int[children.size()];
            int i = 0;
            for (ConceptChild child : children) {
                codes[i] = child.getCode();
                nsIds[i] = nsId;
                i++;
            }
            // This search grabs all of their properties
            DTSConcept[] concepts = SearchQuery.createInstance(conn).findConceptsByCode(codes, nsIds, casd);
            Set<DTSConcept> conceptSet = new HashSet<DTSConcept>(Arrays.asList(concepts));
            // Add root concept if requested
            if (includeRoot) {
                conceptSet.add(rootConcept);
            }
            return conceptSet;
        } catch (ApelonException | ClassNotFoundException | DTSException | IllegalArgumentException e) {
            throw new ApelonClientException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(),e);
                }
            }
        }
    }

    /**
     * Finds concepts that have a property with given property type name and value
     * </br>
     * To find all concepts with given property regardless of value, use "*" for the propertyValuePattern
     * 
     * @param propertyTypeName (e.g. "Description", "Code in Source", etc.)
     * @param propertyValuePattern (e.g. "Best*", "*inner*", etc.)
     * @return Set of DTSConcept with no duplicates or empty set if none found. Never null.
     * @throws Exception on failed connection, invalid propertyTypeName, or Apelon internal error
     */
    @Override
    public Set<DTSConcept> findConceptsWithProperty(String propertyTypeName, String propertyValuePattern)
            throws ApelonClientException {
        ServerConnection conn = null;
        try {
            conn = getSecureSocketConnection();
            SearchQuery sq = SearchQuery.createInstance(conn);
            DTSPropertyType propertyType = findPropertyType(propertyTypeName, sq);
            DTSSearchOptions searchOptions = new DTSSearchOptions(DTSSearchOptions.MAXIMUM_LIMIT, nsId, casd);
            DTSConcept[] concepts = sq.findConceptsWithPropertyMatching(propertyType, propertyValuePattern,
                    searchOptions);
            return new HashSet<DTSConcept>(Arrays.asList(concepts));
        } catch (ApelonException | ClassNotFoundException | DTSException e) {
            throw new ApelonClientException(e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (Exception e) {
                    log.warn(e.getMessage(),e);
                }
            }
        }
    }

    //Might be a bug in ApelonDTS: when property name not found - throws NoClassDefFoundError 
    //instead of DTSException as stated in docs 
    private DTSPropertyType findPropertyType(String propertyTypeName, SearchQuery sq) throws ApelonClientException {
        try {
            return sq.findPropertyTypeByName(propertyTypeName, nsId);
        } catch (DTSException | NoClassDefFoundError e) {
            throw new ApelonClientException("'" + propertyTypeName + "' could not be found as a property type");
        }
    }

    /**
     * Recursively finds all of the children of rootConcept to a given number of levelsDeep
     * 
     * @param rootConcept In a recursive sense, it is the parent of the children being found at current level
     * @param associationType "Parent Of"
     * @param conn {@link ServerConnection} to Apelon host
     * @param levelsDeep The maximum levels of recursion
     * @return Set of DTSConcept with no duplicates
     * @throws DTSException on internal Apelon error
     */
    private Set<ConceptChild> getConceptChildren(DTSConcept rootConcept, AssociationType associationType,
            ServerConnection conn, int levelsDeep) throws DTSException {

        // Set up query
        NavChildContext navChildContext = NavQuery.createInstance(conn).getNavChildContext(rootConcept, casd,
                associationType);
        // Get direct children
        Set<ConceptChild> children = new HashSet<>(Arrays.asList(navChildContext.getChildren()));

        // If there are children find each child's children
        if (!children.isEmpty() && --levelsDeep > 0) {
            Set<ConceptChild> grandChildren = new HashSet<>();
            // For each child
            for (ConceptChild child : children) {
                // If it has children
                if (child.getFetchedHasSubs()) {
                    // Get the children and add them to grandChildren set
                    grandChildren.addAll(getConceptChildren(child, associationType, conn, levelsDeep));
                }
            }
            // Consolidate
            children.addAll(grandChildren);
        }

        return children;
    }

    /**
     * Finds the namespace id for the given namespace
     * 
     * @param namespaceName
     * @return int namespace id
     * @throws Exception on internal Apelon error
     */
    private int findNamespaceId(String namespaceName) throws Exception {
        ServerConnection conn = getSecureSocketConnection();
        try {
            NamespaceQuery namespaceQuery = NamespaceQuery.createInstance(conn);
            Namespace ns = namespaceQuery.findNamespaceByName(namespaceName);
            if (ns == null) {
                return -1;
            } else {
                return ns.getId();
            }
        } finally {
            conn.close();
        }
    }

    /**
     * Gets a secure socket connection using the host URL, port, username, and password provided in the constructor
     * @return {@link ServerConnectionSecureSocket}
     * @throws ClassNotFoundException if Query server class not found
     * @throws ApelonException if ServerConnectionSecureSocket creation f
     */
    private ServerConnection getSecureSocketConnection() throws ClassNotFoundException, ApelonException {

        ServerConnectionSecureSocket secureSocketConnection;

        secureSocketConnection = new ServerConnectionSecureSocket(host, port, username, password);
        secureSocketConnection.setQueryServer(SubsetServer.class,
                com.apelon.dts.client.common.DTSHeader.SUBSETSERVER_HEADER);
        secureSocketConnection.setQueryServer(DTSConceptServer.class,
                com.apelon.dts.client.common.DTSHeader.DTSCONCEPTSERVER_HEADER);
        secureSocketConnection.setQueryServer(OntylogConceptServer.class,
                com.apelon.dts.client.common.DTSHeader.ONTYLOGCONCEPTSERVER_HEADER);
        secureSocketConnection.setQueryServer(NamespaceServer.class,
                com.apelon.dts.client.common.DTSHeader.NAMESPACESERVER_HEADER);
        secureSocketConnection.setQueryServer(SearchQueryServer.class,
                com.apelon.dts.client.common.DTSHeader.SEARCHSERVER_HEADER);
        secureSocketConnection.setQueryServer(AssociationServer.class,
                com.apelon.dts.client.common.DTSHeader.ASSOCIATIONSERVER_HEADER);
        secureSocketConnection.setQueryServer(NavQueryServer.class,
                com.apelon.dts.client.common.DTSHeader.NAVSERVER_HEADER);
        return secureSocketConnection;
    }

}
