package no.jansk.fant;


import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.resource.spi.ConfigProperty;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.StreamingOutput;

/** REST service class to be used by the UI */
public class FantService {
    
    @Inject
    AuthenticationService authService;    
    
    @Inject
    MailService mailService;
    
    @Context
    SecurityContext sc;
        
    @PersistenceContext
    EntityManager em;
    
    /** path to store photos */
    @Inject
    @ConfigProperty(name = "photo.storage.path", defaultValue = "chatphotos")
    String photoPath;
    
    /**
    * Public method that returns items with photos sold in the shop
    */
    public List<Item> getItems() {
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllItems() {
        List<Item> items = ItemRepository.getAllItems(); //queries database for all items
        }
    }
    /**
    * A registered user may purchase an Item. An email will be sent to the
    * seller if the purchase is successful
    *
    * @param itemid unique id for item
    * @return result of purchase request
    */
    public Response purchase(Long itemid) {
        @GET
        @Consumes(MediaType.APPLICATION_JSON)
        public Response getItems(itemid){
            return getItems(itemid)
        }
    }
     
    /**
     * A registered user may remove an item and associated photos owned by the
     * calling user. An user with administrator privileges may remove any item
     * and associated photos.
     *
     * @param itemid unique id for item to be deleted
     * @return result of delete request
     */
    public Response delete(Long itemid) {
        @Delete
        @PATH("{itemid}")
        @Produces(MediaType.APPLICATION_JSON)
        public Response deleteItem(@PathParam("itemid") String itemid) {
            Item item = itemRepository.deleteItemByItemid(itemid);
            return Response.ok(item).build();
        }
    }
    /**
     * A registered user may add an item and photo(s) to Fant.
     *
     * @param title the title of Item
     * @param description the description of Item
     * @param price the price of Item
     * @param photos one or more photos associated with Item
     * @return result of the request. If successful, the request will include
     * the new unique ids of the Item and associated Photos
     */
     public Response addItem(String title, String description, BigDecimal price,
     FormDataMultiPart photos) {
        @POST
        @Consumes (MediaType.APPLICATION_JSON)
        @Produces (MediaType.APPLICATION_JSON)
        public Response saveItem(Item item) {
            item = itemRepository.saveItem(item);
            return Response.ok(item).build();
        }
     }
     /**
     * Streams an image to the browser (the actual compressed pixels). The image
     * will be scaled to the appropriate width if the width parameter is provided.
     * This is a public method available to all callers.
     *
     * @param name the filename of the image
     * @param width the required scaled with of the image
     *
     * @return the image in original format or in jpeg if scaled
     */
         /**
     * Streams an image to the browser(the actual compressed pixels). The image
     * will be scaled to the appropriate width if the with parameter is provided.
     *
     * @param name the filename of the image
     * @param width the required scaled with of the image
     * 
     * @return the image in original format or in jpeg if scaled
     */
    @GET
    @Path("image/{name}")
    @Produces("image/jpeg")
    public Response getPhoto(@PathParam("name") String name, 
                             @QueryParam("width") int width) {
        if(em.find(MediaObject.class, name) != null) {
            StreamingOutput result = (OutputStream os) -> {
                java.nio.file.Path photo = Paths.get(getPhotoPath(),name);
                if(width == 0) {
                    Files.copy(image, os);
                    os.flush();
                } else {
                    Thumbnails.of(image.toFile())
                              .size(width, width)
                              .outputFormat("jpeg")
                              .toOutputStream(os);
                }
            };

            // Ask the browser to cache the image for 24 hours
            CacheControl cc = new CacheControl();
            cc.setMaxAge(86400);
            cc.setPrivate(true);

            return Response.ok(result).cacheControl(cc).build();
        } else {
            return Response.status(Status.NOT_FOUND).build();
        }
    } 
}